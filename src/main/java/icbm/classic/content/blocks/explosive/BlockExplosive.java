package icbm.classic.content.blocks.explosive;

import icbm.classic.ICBMClassic;
import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.reg.IExplosiveData;
import icbm.classic.lib.capability.ex.CapabilityExplosiveStack;
import icbm.classic.lib.transform.vector.Pos;
import icbm.classic.prefab.tile.BlockICBM;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

public class BlockExplosive extends BlockICBM
{
    public static final PropertyExplosive EX_PROP = new PropertyExplosive();

    public BlockExplosive()
    {
        super("explosives", Material.TNT);
        setHardness(2);
        setSoundType(SoundType.CLOTH);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player)
    {
        return getItem(world, pos, getActualState(state, world, pos));
    }

    @Override
    public int damageDropped(BlockState state)
    {
        return state.getValue(EX_PROP).getRegistryID();
    }

    @Override
    public BlockState getActualState(BlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        IExplosiveData explosiveData = null;
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityExplosive && ((TileEntityExplosive) tile).capabilityExplosive != null)
        {
            explosiveData = ((TileEntityExplosive) tile).capabilityExplosive.getExplosiveData();
        }

        if (explosiveData != null)
        {
            return state.withProperty(EX_PROP, explosiveData);
        }
        return state;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean isSideSolid(BlockState base_state, IBlockAccess world, BlockPos pos, Direction side)
    {
        return isNormalCube(base_state, world, pos);
    }

    @Override
    public boolean isTopSolid(BlockState state)
    {
        return true;
    }

    @Override
    public boolean isOpaqueCube(BlockState state)
    {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, ROTATION_PROP, EX_PROP);
    }

    @Override
    public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer, Hand hand)
    {
        ItemStack stack = placer.getHeldItem(hand);
        BlockState state = getDefaultState().withProperty(ROTATION_PROP, facing);
        IExplosiveData prop = ICBMClassicAPI.EXPLOSIVE_REGISTRY.getExplosiveData(stack.getItemDamage());
        if(prop != null) {
            return state.withProperty(EX_PROP, prop);
        }
        else { // if the explosives id doesnt exist, then fallback to the one with the id 0
            prop = ICBMClassicAPI.EXPLOSIVE_REGISTRY.getExplosiveData(0);
            ICBMClassic.logger().log(Level.ERROR, "Unable to get explosives kind, choosing "+prop.getRegistryKey().toString()+" as a fallback.");
            stack.setItemDamage(0);
            return state.withProperty(EX_PROP, prop);
        }
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, BlockState state)
    {
        // Can't be implemented as we lack blockState for explosive at this point
        //super.onBlockAdded(world, pos, state);

        //if (!world.isRemote && world.isBlockPowered(pos))
        //{
        //    BlockExplosive.triggerExplosive(world, pos, false);
        //}
    }

    /**
     * Called when the block is placed in the world.
     */
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entityLiving, ItemStack itemStack)
    {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityExplosive)
        {
            TileEntityExplosive explosive = (TileEntityExplosive) tile;
            explosive.capabilityExplosive = new CapabilityExplosiveStack(itemStack.copy());

            if (world.isBlockPowered(pos))
            {
                BlockExplosive.triggerExplosive(world, pos, false);
            }

            // Check to see if there is fire nearby.
            // If so, then detonate.
            for (Direction rotation : Direction.HORIZONTALS)
            {
                Pos position = new Pos(pos).add(rotation);
                Block blockId = position.getBlock(world);

                if (blockId == net.minecraft.block.Blocks.FIRE || blockId == net.minecraft.block.Blocks.FLOWING_LAVA || blockId == Blocks.LAVA)
                {
                    BlockExplosive.triggerExplosive(world, pos, true);
                    break;
                }
            }

            if (entityLiving != null)
            {
                //TODO turn into event and logger
                ICBMClassic.logger().info("ICBMClassic>>BlockExplosive#onBlockPlacedBy: " + entityLiving.getName()
                        + " placed " + explosive.capabilityExplosive.getExplosiveData().getRegistryKey() + " in: " + pos);
            }
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed
     * (coordinates passed are their own) Args: x, y, z, neighbor block
     */
    @Override
    public void neighborChanged(BlockState thisBlock, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (world.isBlockPowered(pos))
        {
            BlockExplosive.triggerExplosive(world, pos, false);
        }
    }

    /*
     * Called to detonate the TNT. Args: world, x, y, z, metaData, CauseOfExplosion (0, intentional,
     * 1, exploded, 2 burned)
     */

    public static void triggerExplosive(World world, BlockPos pos, boolean setFire)
    {
        if (!world.isRemote)
        {
            TileEntity tileEntity = world.getTileEntity(pos);

            if (tileEntity instanceof TileEntityExplosive)
            {
                ((TileEntityExplosive) tileEntity).trigger(setFire);
            }
        }
    }

    /**
     * Called upon the block being destroyed by an explosion
     */
    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion)
    {
        BlockExplosive.triggerExplosive(world, pos, false);
        super.onBlockExploded(world, pos, explosion);
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosionIn)
    {
        return false;
    }

    /**
     * Called upon block activation (left or right click on the block.). The three integers
     * represent x,y,z of the block.
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, float hitX, float hitY, float hitZ)
    {
        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty() && (itemstack.getItem() == Items.FLINT_AND_STEEL || itemstack.getItem() == Items.FIRE_CHARGE))
        {
            BlockExplosive.triggerExplosive(world, pos, false);

            if (itemstack.getItem() == Items.FLINT_AND_STEEL)
            {
                itemstack.damageItem(1, player);
            }
            else if (!player.capabilities.isCreativeMode)
            {
                itemstack.shrink(1);
            }

            return true;
        }
        else
        {
            return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, BlockState state, Entity entityIn)
    {
        if (!world.isRemote && entityIn instanceof AbstractArrowEntity)
        {
            AbstractArrowEntity entityarrow = (AbstractArrowEntity)entityIn;

            if (entityarrow.isBurning())
            {
                BlockExplosive.triggerExplosive(world, pos, false);
            }
        }
    }

    @Override
    public void getSubBlocks(ItemGroup tab, NonNullList<ItemStack> items)
    {
        if (tab == this.getCreativeTabToDisplayOn())
        {
            for (int id : ICBMClassicAPI.EX_BLOCK_REGISTRY.getExplosivesIDs())
            {
                items.add(new ItemStack(this, 1, id));
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityExplosive();
    }
}
