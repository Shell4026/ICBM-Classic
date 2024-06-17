package icbm.classic.content.blocks.radarstation;

import icbm.classic.ICBMClassic;
import icbm.classic.prefab.tile.BlockICBM;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 *
 * Created by Dark(DarkGuardsman, Robin) on 1/16/2018.
 */
public class BlockRadarStation extends BlockICBM
{
    public static final PropertyBool REDSTONE_PROPERTY = PropertyBool.create("redstone");
    public static final PropertyRadarState RADAR_STATE = new PropertyRadarState();

    public BlockRadarStation()
    {
        super("radarStation"); //TODO rename to "radar_screen"
        this.dropInventory = true;
    }

    @Override
    public BlockState getActualState(BlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        final TileEntity tile = worldIn.getTileEntity(pos);
        if(tile instanceof TileRadarStation) {
            return state.withProperty(RADAR_STATE, ((TileRadarStation) tile).getRadarState());
        }
        return state;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, ROTATION_PROP, REDSTONE_PROPERTY, RADAR_STATE);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockAccess world, BlockPos pos, @Nullable Direction side)
    {
        final TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof TileRadarStation) {
            return ((TileRadarStation) tileEntity).isOutputRedstone();
        }
        return false;
    }

    @Override
    public boolean canProvidePower(BlockState state)
    {
        return state.getValue(REDSTONE_PROPERTY);
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockAccess blockAccess, BlockPos pos, Direction side)
    {
        return getStrongPower(blockState, blockAccess, pos, side);
    }

    @Override
    public int getStrongPower(BlockState blockState, IBlockAccess blockAccess, BlockPos pos, Direction side)
    {
        TileEntity tile = blockAccess.getTileEntity(pos);
        if (tile instanceof TileRadarStation)
        {
            return ((TileRadarStation) tile).getStrongRedstonePower(side);
        }
        return 0;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            //if (WrenchUtility.isUsableWrench(player, player.getHeldItem(hand), pos.getX(), pos.getY(), pos.getZ()))
            if (player.getHeldItem(hand).getItem() == Items.REDSTONE) //TODO move to UI
            {
                final TileEntity tile = world.getTileEntity(pos);
                if (tile instanceof TileRadarStation)
                {
                    ((TileRadarStation) tile).setOutputRedstone(!((TileRadarStation) tile).isOutputRedstone());
                    player.sendMessage(new TranslationTextComponent(((TileRadarStation) tile).isOutputRedstone() ? "message.radar.redstone.on" : "message.radar.redstone.off"));
                }
                else
                {
                    player.sendMessage(new StringTextComponent("\u00a7cUnexpected error: Couldn't access radar station tile"));
                }
            }
            else
            {
                player.openGui(ICBMClassic.INSTANCE, 0, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileRadarStation();
    }
}
