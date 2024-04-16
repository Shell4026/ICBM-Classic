package icbm.classic.config.util;



import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class ItemStackConfigList extends ResourceConfigList<ItemStackConfigList, ItemStack> {
    public ItemStackConfigList(String name, Consumer<ItemStackConfigList> reloadCallback) {
        super(name, reloadCallback);
    }

    @Override
    public boolean contains(ItemStack state) {
        if (state == null || state.isEmpty()) {
            return false;
        }
        //TODO find a way to cache ItemStack for faster performance
        return super.contains(state);
    }

    //TODO add support for metadata
    //TODO add support for NBT

    @Override
    protected ResourceLocation getContentKey(ItemStack itemStack) {
        return itemStack.getItem().getRegistryName();
    }
}
