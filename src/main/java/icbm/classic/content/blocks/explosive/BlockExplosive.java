package icbm.classic.content.blocks.explosive;

import icbm.classic.ICBMClassic;
import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.actions.IActionData;
import icbm.classic.api.actions.cause.IActionSource;
import icbm.classic.api.actions.data.ActionFields;
import icbm.classic.api.refs.ICBMExplosives;
import icbm.classic.api.reg.IExplosiveData;
import icbm.classic.content.entity.EntityExplosive;
import icbm.classic.content.missile.logic.source.ActionSource;
import icbm.classic.lib.actions.fields.ActionFieldProvider;
import icbm.classic.lib.capability.ex.CapabilityExplosiveStack;
import icbm.classic.lib.transform.vector.Pos;
import icbm.classic.prefab.tile.BlockICBM;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;

public class BlockExplosive extends Block
{
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public final IExplosiveData explosiveData;

    public BlockExplosive(IExplosiveData explosiveData)
    {
        super(Block.Properties.create(Material.TNT).hardnessAndResistance(2));
        this.explosiveData = explosiveData;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }


    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState().with(FACING, context.getFace());
    }

    @Override
    public void catchFire(BlockState state, World world, BlockPos pos, @Nullable net.minecraft.util.Direction face, @Nullable LivingEntity igniter) {
        explode(world, pos, igniter);
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (oldState.getBlock() != state.getBlock()) {
            if (worldIn.isBlockPowered(pos)) {
                catchFire(state, worldIn, pos, null, null);
                worldIn.removeBlock(pos, false);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (worldIn.isBlockPowered(pos)) {
            catchFire(state, worldIn, pos, null, null);
            worldIn.removeBlock(pos, false);
        }
    }

    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
        if (!worldIn.isRemote) {
            TNTEntity tntentity = new TNTEntity(worldIn, (double)((float)pos.getX() + 0.5F), (double)pos.getY(), (double)((float)pos.getZ() + 0.5F), explosionIn.getExplosivePlacedBy());
            tntentity.setFuse((short)(worldIn.rand.nextInt(tntentity.getFuse() / 4) + tntentity.getFuse() / 8));
            worldIn.addEntity(tntentity);
        }
    }

    public void explode(World world, BlockPos pos, LivingEntity causedBy)
    {
        if (!world.isRemote)
        {
            final BlockState state = world.getBlockState(pos);
            if(explosiveData == ICBMExplosives.BREACHING) {
                final IActionSource source = new ActionSource(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), null);
                final Direction direction = state.get(FACING).getOpposite();
                explosiveData
                    .create(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, source, new ActionFieldProvider().field(ActionFields.HOST_DIRECTION, () -> direction))
                    .doAction();
            }
            else {

                EntityExplosive entityExplosive = new EntityExplosive(world, new Pos(pos).add(0.5), state.get(FACING), new ItemStack(this));
                //TODO check for tick rate, trigger directly if tick is less than 3

                //if (setFire) { TODO is this used?
                //    entityExplosive.setFire(100);
                //}

                world.addEntity(entityExplosive);
            }
        }
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosionIn)
    {
        return false;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack itemstack = player.getHeldItem(handIn);
        Item item = itemstack.getItem();
        if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE) {
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
        } else {
            catchFire(state, worldIn, pos, hit.getFace(), player);
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
            if (item == Items.FLINT_AND_STEEL) {
                itemstack.damageItem(1, player, (p_220287_1_) -> {
                    p_220287_1_.sendBreakAnimation(handIn);
                });
            } else {
                itemstack.shrink(1);
            }

            return true;
        }
    }

    @Override
    public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, Entity projectile) {
        if (!worldIn.isRemote && projectile instanceof AbstractArrowEntity) {
            AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity)projectile;
            Entity entity = abstractarrowentity.getShooter();
            if (abstractarrowentity.isBurning()) {
                BlockPos blockpos = hit.getPos();
                catchFire(state, worldIn, blockpos, null, entity instanceof LivingEntity ? (LivingEntity)entity : null);
                worldIn.removeBlock(blockpos, false);
            }
        }

    }
}
