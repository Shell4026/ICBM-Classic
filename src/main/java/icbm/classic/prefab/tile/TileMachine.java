package icbm.classic.prefab.tile;

import icbm.classic.lib.tile.ITick;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dark(DarkGuardsman, Robin) on 1/9/2017.
 */
public class TileMachine extends TileEntity implements ITickable
{
    protected int ticks = -1;

    protected final List<ITick> tickActions = new ArrayList();

    public TileMachine(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }

    @Override
    public void tick()
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
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return write(new CompoundNBT());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        read(pkt.getNbtCompound());
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
        if (state.has(BlockICBM.ROTATION_PROP))
        {
            return state.get(BlockICBM.ROTATION_PROP);
        }
        return Direction.NORTH;
    }

    public BlockState getBlockState()
    {
        return world.getBlockState(getPos());
    }
}
