package icbm.classic.lib.saving.nodes;

import icbm.classic.lib.saving.NbtSaveNode;
import net.minecraft.nbt.StringNBT;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SaveNodeString<E> extends NbtSaveNode<E, StringNBT>
{
    public SaveNodeString(final String name, Function<E, String> save, BiConsumer<E, String> load)
    {
        super(name,
            (obj) -> {
                final String str = save.apply(obj);
                return str != null ? new StringNBT(str) : null;
            },
            (obj, data) -> load.accept(obj, data.getString())
        );
    }
}
