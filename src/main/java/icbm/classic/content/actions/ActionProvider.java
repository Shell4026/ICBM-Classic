package icbm.classic.content.actions;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.actions.IActionProvider;
import icbm.classic.api.actions.IPotentialAction;
import icbm.classic.api.data.meta.MetaTag;
import net.minecraft.nbt.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * General use action provider for dynamic action setups
 */
public final class ActionProvider implements IActionProvider, INBTSerializable<CompoundNBT> {
    private final Map<MetaTag, IPotentialAction> actions = new HashMap();

    public ActionProvider withAction(MetaTag tag, IPotentialAction action) {
        actions.put(tag, action);
        return this;
    }

    @Nullable
    @Override
    public IPotentialAction getPotentialAction(MetaTag key) {
        return actions.get(key); //TODO do key#isSubType
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        ListNBT list = new ListNBT();
        actions.forEach((tag, action) -> {
            final CompoundNBT entry = new CompoundNBT();
            entry.setString("key", tag.getKey());
            entry.setTag("value", ICBMClassicAPI.ACTION_POTENTIAL_REGISTRY.save(action));
            list.appendTag(entry);
        });
        compound.setTag("actions", list);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if(nbt.hasKey("actions")) {
            ListNBT list = nbt.getTagList("actions", 10);
            actions.clear();
            for (int i = 0; i < list.tagCount(); i++) {
                CompoundNBT entry = list.getCompoundTagAt(i);
                String keyString = entry.getString("key");
                CompoundNBT valueTag = entry.getCompoundTag("value");
                MetaTag key = MetaTag.find(keyString);
                IPotentialAction value = ICBMClassicAPI.ACTION_POTENTIAL_REGISTRY.load(valueTag);
                actions.put(key, value);
            }
        }
    }

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IActionProvider.class, new Capability.IStorage<IActionProvider>()
            {

                @Nullable
                @Override
                public NBTBase writeNBT(Capability<IActionProvider> capability, IActionProvider instance, Direction side) {
                    return instance instanceof INBTSerializable ? ((INBTSerializable<?>) instance).serializeNBT() : null;
                }

                @Override
                public void readNBT(Capability<IActionProvider> capability, IActionProvider instance, Direction side, NBTBase nbt) {
                    if(instance instanceof INBTSerializable) {
                        ((INBTSerializable<NBTBase>) instance).deserializeNBT(nbt);
                    }
                }
            },
            ActionProvider::new);
    }
}
