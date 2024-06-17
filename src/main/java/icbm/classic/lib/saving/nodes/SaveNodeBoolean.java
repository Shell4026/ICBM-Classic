package icbm.classic.lib.saving.nodes;

import icbm.classic.lib.saving.NbtSaveNode;
import net.minecraft.nbt.ByteNBT;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SaveNodeBoolean<E> extends NbtSaveNode<E, ByteNBT>
{
    public SaveNodeBoolean(final String name, Function<E, Boolean> save, BiConsumer<E, Boolean> load)
    {
        super(name,
            (obj) -> save(save.apply(obj)),
            (obj, data) -> load.accept(obj, load(data))
        );
    }

    public static ByteNBT save(Boolean b) {
        return new ByteNBT((byte) (b ? 1 : 0));
    }

    public static boolean load(ByteNBT tag) {
        return tag.getByte() == 1;
    }
}
