package icbm.classic.prefab.tile;

import icbm.classic.lib.tile.ITick;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dark(DarkGuardsman, Robin) on 1/9/2017.
 */
public class TileMachine extends TileEntity implements ITickable
{
    protected int ticks = -1;

    protected final List<ITick> tickActions = new ArrayList();

    @Override
    public void update()
    {
        //Increase tick
        ticks++;
        if (ticks >= Integer.MAX_VALUE - 1)
        {
            ticks = 0;
        }

        tickActions.forEach(action -> {
            action.update(ticks, isServer());
        });
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return writeToNBT(new CompoundNBT());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    public boolean isServer()
    {
        return world != null && !world.isRemote;
    }

    public boolean isClient()
    {
        return world != null && world.isRemote;
    }

    @Deprecated
    public Direction getRotation()
    {
        BlockState state = getBlockState();
        if (state.getProperties().containsKey(BlockICBM.ROTATION_PROP))
        {
            return state.getValue(BlockICBM.ROTATION_PROP);
        }
        return Direction.NORTH;
    }

    public BlockState getBlockState()
    {
        return world.getBlockState(getPos());
    }
}
