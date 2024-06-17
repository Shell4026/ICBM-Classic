package icbm.classic.lib.saving.nodes;

import icbm.classic.lib.saving.NbtSaveNode;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SaveNodeBlockPos<E> extends NbtSaveNode<E, CompoundNBT>
{
    public SaveNodeBlockPos(final String name, Function<E, BlockPos> save, BiConsumer<E, BlockPos> load) {
        super(name,
            (obj) -> save(save.apply(obj)),
            (obj, data) -> load.accept(obj, load(data))
        );
    }

    public static CompoundNBT save(BlockPos pos) {
        if(pos != null) {
            final CompoundNBT compound = new CompoundNBT();
            compound.setInteger("x", pos.getX());
            compound.setInteger("y", pos.getY());
            compound.setInteger("z", pos.getZ());
            return compound;
        }
        return null;
    }

    public static BlockPos load(CompoundNBT data) {
        return new BlockPos(
            data.getInteger("x"),
            data.getInteger("y"),
            data.getInteger("z")
        );
    }
}
