package icbm.classic.lib.saving.nodes;

import icbm.classic.lib.saving.NbtSaveNode;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SaveNodeResourceLocation<E> extends NbtSaveNode<E, StringNBT>
{
    public SaveNodeResourceLocation(final String name, Function<E, ResourceLocation> save, BiConsumer<E, ResourceLocation> load)
    {
        super(name,
            (obj) -> SaveNodeResourceLocation.save(save.apply(obj)),
            (obj, data) -> {
                load.accept(obj, SaveNodeResourceLocation.load(data));
            }
        );
    }

    public static StringNBT save(ResourceLocation key) {
        if(key == null) {
            return null;
        }
        return new StringNBT(key.toString());
    }

    public static ResourceLocation load(StringNBT data) {
        return new ResourceLocation(data.getString());
    }
}
