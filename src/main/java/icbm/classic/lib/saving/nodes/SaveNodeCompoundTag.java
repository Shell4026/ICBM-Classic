package icbm.classic.lib.saving.nodes;

import icbm.classic.lib.saving.NbtSaveNode;
import net.minecraft.nbt.CompoundNBT;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SaveNodeCompoundTag<E> extends NbtSaveNode<E, CompoundNBT>
{
    public SaveNodeCompoundTag(final String name, Function<E, CompoundNBT> getter, BiConsumer<E, CompoundNBT> setter) {
        super(name, getter, setter);
    }
}
