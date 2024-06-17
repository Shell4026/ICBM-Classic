package icbm.classic.content.blocks.launcher.connector;

import icbm.classic.ICBMClassic;
import icbm.classic.ICBMConstants;
import icbm.classic.content.blocks.launcher.network.ILauncherComponent;
import icbm.classic.content.blocks.launcher.network.LauncherNetwork;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

/**
 *
 * Created by Dark(DarkGuardsman, Robin) on 1/16/2018.
 */
public class BlockLaunchConnector extends ContainerBlock
{
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");

    public BlockLaunchConnector()
    {
        super(Material.IRON);
        blockHardness = 10f;
        blockResistance = 10f;
        setRegistryName(ICBMConstants.DOMAIN, "launcher_connector");
        setUnlocalizedName(ICBMConstants.PREFIX + "launcher_connector");
        setCreativeTab(ICBMClassic.CREATIVE_TAB);
    }

    @Override
    public BlockState getActualState(BlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        final boolean upConnection = isConnection(worldIn, pos, Direction.UP);
        final boolean downConnection = isConnection(worldIn, pos, Direction.DOWN);
        final boolean northConnection = isConnection(worldIn, pos, Direction.NORTH);
        final boolean eastConnection = isConnection(worldIn, pos, Direction.EAST);
        final boolean southConnection = isConnection(worldIn, pos, Direction.SOUTH);
        final boolean westConnection = isConnection(worldIn, pos, Direction.WEST);

        return state
            .withProperty(UP, upConnection)
            .withProperty(DOWN, downConnection)
            .withProperty(NORTH, northConnection)
            .withProperty(EAST, eastConnection)
            .withProperty(SOUTH, southConnection)
            .withProperty(WEST, westConnection);
    }

    private boolean isConnection(IBlockAccess worldIn, BlockPos selfPos, Direction side) {
        final BlockPos pos = selfPos.offset(side);
        final TileEntity tile = worldIn.getTileEntity(pos);
        if(tile != null) {
            return tile.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite())
                || tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());
        }
        return false;
    }


    @Override
    public BlockState getStateFromMeta(int meta)
    {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(BlockState state)
    {
        return 0;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, float hitX, float hitY, float hitZ)
    {
        final TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof icbm.classic.content.blocks.launcher.frame.TileLauncherFrame)
        {
            if(playerIn.getHeldItem(hand).getItem() == Items.STONE_AXE) {
                if(!worldIn.isRemote) {
                    final LauncherNetwork network = ((icbm.classic.content.blocks.launcher.frame.TileLauncherFrame) tile).getNetworkNode().getNetwork();
                    playerIn.sendMessage(new StringTextComponent("Network: " + network));
                    playerIn.sendMessage(new StringTextComponent("L: " + network.getLaunchers().size()));
                }
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public boolean isOpaqueCube(BlockState state)
    {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, UP, DOWN, NORTH, EAST, WEST, SOUTH);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileLauncherConnector();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, BlockState state)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ILauncherComponent)
        {
            ((ILauncherComponent) tile).getNetworkNode().onTileRemoved();
        }
        super.breakBlock(world, pos, state);
    }
}
