package icbm.classic.config.util;



import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Documentation <a href="https://github.com/BuiltBrokenModding/ICBM-Classic/wiki/config-itemstack">ItemStack Config</a>
 * @param <VALUE> to use
 */
public abstract class ItemStackConfigList<VALUE> extends ResourceConfigList<ItemStackConfigList, ItemStack, VALUE> {
    public ItemStackConfigList(String name, Consumer<ItemStackConfigList> reloadCallback) {
        super(name, reloadCallback);
    }

    @Override
    public VALUE getValue(ItemStack state) {
        if (state == null || state.isEmpty()) {
            return null;
        }
        //TODO find a way to cache ItemStack for faster performance
        return super.getValue(state);
    }

    //TODO add support for metadata
    //TODO add support for NBT

    @Override
    protected ResourceLocation getContentKey(ItemStack itemStack) {
        return itemStack.getItem().getRegistryName();
    }

    public static class BooleanOut extends ItemStackConfigList<Boolean> {

        public BooleanOut(String name, Consumer<ItemStackConfigList> reloadCallback) {
            super(name, reloadCallback);
        }

        @Override
        protected Function<ItemStack, Boolean> getDomainValue(String domain, Boolean disable) {
            if(Boolean.TRUE.equals(disable)) {
                return null;
            }
            return (stack) -> getContentKey(stack).getResourceDomain().equalsIgnoreCase(domain);
        }

        @Override
        protected Function<ItemStack, Boolean> getSimpleValue(ResourceLocation key, Boolean disable) {
            if(Boolean.TRUE.equals(disable)) {
                return null;
            }
            return (stack) -> getContentKey(stack) == key;
        }

        @Override
        protected Boolean parseValue(@Nullable String value) {
            return Boolean.parseBoolean(value);
        }
    }

    public static class IntOut extends ItemStackConfigList<Integer> {

        public IntOut(String name, Consumer<ItemStackConfigList> reloadCallback) {
            super(name, reloadCallback);
        }

        @Override
        protected Function<ItemStack, Integer> getDomainValue(String domain, @Nullable Integer value) {
            return (stack) -> {
                if(getContentKey(stack).getResourceDomain().equalsIgnoreCase(domain)) {
                    return value;
                }
                return null;
            };
        }

        @Override
        protected Function<ItemStack, Integer> getSimpleValue(ResourceLocation key, @Nullable Integer value) {
            return (stack) -> {
                if(getContentKey(stack) == key) {
                    return value;
                }
                return null;
            };
        }

        @Override
        protected Integer parseValue(@Nullable String value) {
            return value == null ? null : Integer.parseInt(value, 10);
        }
    }
}
