package icbm.classic.content.blocks.launcher.base;

import icbm.classic.ICBMClassic;
import icbm.classic.ICBMConstants;
import icbm.classic.content.blocks.launcher.network.ILauncherComponent;
import icbm.classic.content.blocks.launcher.network.LauncherNetwork;
import icbm.classic.lib.InventoryUtility;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 *
 * Created by Dark(DarkGuardsman, Robin) on 1/8/2018.
 */
public class BlockLauncherBase extends ContainerBlock
{
    public static final PropertyDirection ROTATION_PROP = PropertyDirection.create("facing");

    public BlockLauncherBase()
    {
        super(Material.IRON);
        this.blockHardness = 10f;
        this.blockResistance = 10f;
        this.fullBlock = true;
        setRegistryName(ICBMConstants.DOMAIN, "launcherbase");
        setUnlocalizedName(ICBMConstants.PREFIX + "launcherbase");
        setCreativeTab(ICBMClassic.CREATIVE_TAB);
    }

    @Deprecated
    public boolean isOpaqueCube(BlockState state)
    {
        // Needed to prevent render lighting issues for missiles
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, float hitX, float hitY, float hitZ)
    {
        final TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileLauncherBase && !worldIn.isRemote)
        {
            if(playerIn.getHeldItem(hand).getItem() == Items.STONE_AXE) {
                    final LauncherNetwork network = ((TileLauncherBase) tile).getNetworkNode().getNetwork();
                    playerIn.sendMessage(new StringTextComponent("Network: " + network));
                    playerIn.sendMessage(new StringTextComponent("L: " + network.getLaunchers().size()));
                return true;
            }
            if(!((TileLauncherBase) tile).tryInsertMissile(playerIn, hand, playerIn.getHeldItem(hand))) {
                playerIn.openGui(ICBMClassic.INSTANCE, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, BlockState state)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ILauncherComponent)
        {
            ((ILauncherComponent) tile).getNetworkNode().onTileRemoved();
        }
        InventoryUtility.dropInventory(world, pos);
        super.breakBlock(world, pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.SOLID;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileLauncherBase();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, ROTATION_PROP);
    }

    @Override
    public BlockState getStateFromMeta(int meta)
    {
        if(meta == 0) {
            return getDefaultState().withProperty(ROTATION_PROP, Direction.UP);
        }
        return getDefaultState().withProperty(ROTATION_PROP, Direction.getFront(meta - 1));
    }

    @Override
    public int getMetaFromState(BlockState state)
    {
        // Shifting by one due to older tiles not having rotation, default should be UP
        return state.getValue(ROTATION_PROP).ordinal() + 1;
    }

    @Override
    public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer, Hand hand)
    {
        return getDefaultState().withProperty(ROTATION_PROP, facing);
    }
}
