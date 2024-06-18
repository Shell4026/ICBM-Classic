package icbm.classic.content.blocks.launcher.base;

import icbm.classic.ICBMClassic;
import icbm.classic.content.blocks.launcher.network.ILauncherComponent;
import icbm.classic.content.blocks.launcher.network.LauncherNetwork;
import icbm.classic.lib.InventoryUtility;
import net.minecraft.block.Block;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 *
 * Created by Dark(DarkGuardsman, Robin) on 1/8/2018.
 */
public class BlockLauncherBase extends ContainerBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public BlockLauncherBase()
    {
        super(Block.Properties.create(Material.IRON).hardnessAndResistance(10, 10));
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
        super.onPlayerDestroy();
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
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.SOLID;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader reader)
    {
        return new TileLauncherBase();
    }


    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return getDefaultState().with(FACING, context.getFace());
    }
}
