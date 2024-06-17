package icbm.classic.lib.saving.nodes;

import icbm.classic.lib.saving.NbtSaveNode;
import net.minecraft.nbt.FloatNBT;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SaveNodeFloat<E> extends NbtSaveNode<E, FloatNBT>
{
    public SaveNodeFloat(final String name, Function<E, Float> save, BiConsumer<E, Float> load)
    {
        super(name,
            (obj) -> new FloatNBT(save.apply(obj)),
            (obj, data) -> load.accept(obj, data.getFloat())
        );
    }
}
