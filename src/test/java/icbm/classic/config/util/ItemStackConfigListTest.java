package icbm.classic.config.util;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Bootstrap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

class ItemStackConfigListTest {

    @BeforeAll
    static void beforeAll() {
        Bootstrap.register();
    }

    @Nested
    class GetValueTests {
        @Test
        void nullTest() {
            final StubbedList configList = new ItemStackConfigListTest.StubbedList("test", (config) -> {});
            Assertions.assertNull(configList.getValue(null));
        }

        @Test
        void emptyTest() {
            final StubbedList configList = new ItemStackConfigListTest.StubbedList("test", (config) -> {});
            Assertions.assertNull(configList.getValue(ItemStack.EMPTY));
        }

        @Test
        void itemStackWithValue() {
            final StubbedList configList = new ItemStackConfigListTest.StubbedList("valueTest", (config) -> {});
            configList.setDefault(new ResourceLocation("minecraft:dirt"), 1, 0);
            Assertions.assertEquals(1, configList.getValue(new ItemStack(net.minecraft.block.Blocks.DIRT)));
        }

        @Test
        void itemStackWithoutValue() {
            final StubbedList configList = new ItemStackConfigListTest.StubbedList("valueTest", (config) -> {});
            configList.setDefault(new ResourceLocation("minecraft:dirt"), 1, 0);
            Assertions.assertNull(configList.getValue(new ItemStack(Blocks.STONE)));
        }
    }



    public static class StubbedList extends ItemStackConfigList {

        public StubbedList(String name, Consumer reloadCallback) {
            super(name, reloadCallback);
        }

        @Override
        protected Object parseValue(String source, String entry, String value) {
            return null;
        }
    }
}