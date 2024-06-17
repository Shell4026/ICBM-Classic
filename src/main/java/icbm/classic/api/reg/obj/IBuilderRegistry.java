package icbm.classic.api.reg.obj;

import icbm.classic.ICBMClassic;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Supplier;

public interface IBuilderRegistry<Part extends IBuildableObject> {

    /**
     * Registers a new factory for loading the part
     *
     * @param name to register with
     * @param builder to create new instances
     *
     * @throws RuntimeException if registry is locked or name is already used
     */
    void register(@Nonnull ResourceLocation name, @Nonnull Supplier<Part> builder);

    /**
     * Builds/Get the object. Often for data sources this is
     * a global static version that is never rebuilt.
     *
     * @param name matching registry
     * @return instance or null if not registered
     */
    @Nullable
    Part getOrBuild(@Nonnull ResourceLocation name);

    /**
     * Unique name for this registry. Mostly used for translations
     *
     * @return unique builder key
     */
    @Nonnull
    String getUniqueName();

    default ListNBT save(@Nonnull Collection<Part> parts) {
        final ListNBT list = new ListNBT();
        for(Part part : parts) {
            final CompoundNBT save = save(part);
            if(save != null) {
                list.appendTag(save);
            }
        }
        return list;
    }

    default CompoundNBT save(@Nonnull Part part) {
        if(part == null) {
            ICBMClassic.logger().warn("Failed to save part due to null value", new RuntimeException());
            return null;
        }
        else if(part.getRegistryKey() == null) {
            ICBMClassic.logger().warn("Failed to save part due to missing registry name: " + part, new RuntimeException());
            return null;
        }

        final CompoundNBT save = new CompoundNBT();
        save.setString("id", part.getRegistryKey().toString());

        if(part instanceof INBTSerializable) {
            // Data is optional, only id is required as some objects are constants and need no save info
            final NBTBase additionalData = ((INBTSerializable<NBTBase>)part).serializeNBT();
            if (additionalData != null && (!additionalData.hasNoTags() || additionalData instanceof NumberNBT)) {
                save.setTag("data", additionalData);
            }
        }

        return save;
    }

    default <C extends Collection<Part>> C load(@Nonnull ListNBT save, @Nonnull C list) {
        for(int i = 0; i < save.tagCount(); i++) {
            final Part part = load((CompoundNBT) save.get(i));
            if(part != null) {
                list.add(part);
            }
        }
        return list;
    }

    default Part load(@Nullable CompoundNBT save) {
        if(save != null && !save.hasNoTags() && save.hasKey("id")) {
            final ResourceLocation id = new ResourceLocation(save.getString("id"));
            final Part part = getOrBuild(id);
            if(part instanceof INBTSerializable && save.hasKey("data")) {
                final NBTBase additionalData = save.getTag("data");
                ((INBTSerializable<NBTBase>)part).deserializeNBT(additionalData);
            }
            return part;
        }
        return null;
    }

    boolean isLocked();
}
