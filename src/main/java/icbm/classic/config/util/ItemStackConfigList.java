package icbm.classic.config.util;



import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Documentation <a href="https://github.com/BuiltBrokenModding/ICBM-Classic/wiki/config-itemstack">ItemStack Config</a>
 * @param <VALUE> to use
 */
public abstract class ItemStackConfigList<VALUE> extends ResourceConfigList<ItemStackConfigList, ItemStack, VALUE> {
    public ItemStackConfigList(String name, Consumer<ItemStackConfigList> reloadCallback) {
        super(name, "https://github.com/BuiltBrokenModding/ICBM-Classic/wiki/config-itemstack", reloadCallback);
    }

    @Override
    public VALUE getValue(ItemStack state) {
        if (state == null || state.isEmpty()) {
            return null;
        }
        //TODO find a way to cache ItemStack for faster performance
        return super.getValue(state);
    }

    public void setDefaultMeta(ItemStack content, VALUE value, int order) {
        final ResourceLocation key = content.getItem().getRegistryName();
        defaultMatchers
            .computeIfAbsent(key, k -> new ArrayList<>())
            .add(new ResourceConfigEntry<>(order, (itemStack) -> ItemStack.areItemsEqual(itemStack, content) ? value : null));
    }

    //TODO add support for NBT

    @Override
    protected Function<ItemStack, VALUE> getDomainValue(String domain, @Nullable VALUE value) {
        return (stack) -> {
            if(getContentKey(stack).getResourceDomain().equalsIgnoreCase(domain)) {
                return value;
            }
            return null;
        };
    }

    @Override
    protected Function<ItemStack, VALUE> getSimpleValue(ResourceLocation key, @Nullable VALUE value) {
        return (stack) -> {
            if(getContentKey(stack) == key) {
                return value;
            }
            return null;
        };
    }

    @Override
    protected Function<ItemStack, VALUE> getMetaValue(ResourceLocation key, int metadata, @Nullable VALUE value) {
        return (stack) -> {
            if(getContentKey(stack) == key && stack.getMetadata() == metadata) {
                return value;
            }
            return null;
        };
    }

    @Override
    protected ResourceLocation getContentKey(ItemStack itemStack) {
        return itemStack.getItem().getRegistryName();
    }

    public static class ContainsCheck extends ItemStackConfigList<Boolean> {

        public ContainsCheck(String name, Consumer<ItemStackConfigList> reloadCallback) {
            super(name, reloadCallback);
        }

        public boolean isAllowed(ItemStack stack) {
            Boolean value = super.getValue(stack);
            return value == null || value;
        }

        @Override
        protected Function<ItemStack, Boolean> getDomainValue(String domain, Boolean disable) {
            if(Boolean.TRUE.equals(disable)) {
                return null;
            }
            return super.getDomainValue(domain, true);
        }

        @Override
        protected Function<ItemStack, Boolean> getSimpleValue(ResourceLocation key, Boolean disable) {
            if(Boolean.TRUE.equals(disable)) {
                return null;
            }
            return super.getSimpleValue(key, true);
        }

        @Override
        protected Function<ItemStack, Boolean> getMetaValue(ResourceLocation key, int metadata, @Nullable Boolean disable) {
            if(Boolean.TRUE.equals(disable)) {
                return null;
            }
            return super.getMetaValue(key, metadata, true);
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
        protected Integer parseValue(@Nullable String value) {
            return value == null ? null : Integer.parseInt(value, 10);
        }
    }

    public static class FloatOut extends ItemStackConfigList<Float> {

        public FloatOut(String name, Consumer<ItemStackConfigList> reloadCallback) {
            super(name, reloadCallback);
        }

        @Override
        protected Float parseValue(@Nullable String value) {
            return value == null ? null : Float.parseFloat(value);
        }
    }
}
