package icbm.classic.lib.saving.nodes;

import icbm.classic.lib.saving.NbtSaveNode;
import net.minecraft.nbt.DoubleNBT;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SaveNodeDouble<E> extends NbtSaveNode<E, DoubleNBT>
{
    public SaveNodeDouble(final String name, Function<E, Double> save, BiConsumer<E, Double> load)
    {
        super(name,
            (obj) -> new DoubleNBT(save.apply(obj)),
            (obj, data) -> load.accept(obj, data.getDouble())
        );
    }
}
