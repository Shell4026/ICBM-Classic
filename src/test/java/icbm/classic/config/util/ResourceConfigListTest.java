package icbm.classic.config.util;


import icbm.classic.ICBMClassic;
import net.minecraft.util.ResourceLocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;
import org.mockito.Mockito;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

class ResourceConfigListTest {

    @ParameterizedTest
    @MethodSource("compareData")
    void compare(ResourceConfigEntry a, ResourceConfigEntry b, int result) {

        final ResourceConfigList configList = new StubbedList("stub", (c) -> {});
        Assertions.assertEquals(result, configList.compare(a, b));
    }

    private static Stream<Arguments> compareData() {
        return Stream.of(
            Arguments.of(new ResourceConfigEntry(1, null), new ResourceConfigEntry(0, null), -1),
            Arguments.of(new ResourceConfigEntry(0, null), new ResourceConfigEntry(1, null), 1),
            Arguments.of(new ResourceConfigEntry(0, null), new ResourceConfigEntry(0, null), 0)
        );
    }

    @Test
    void sort() {

        final ResourceConfigList configList = new StubbedList("stub", (c) -> {});

        final List<ResourceConfigEntry> list = new ArrayList();
        list.add(new ResourceConfigEntry(3, null));
        list.add(new ResourceConfigEntry(17, null));
        list.add(new ResourceConfigEntry(8, null));
        list.add(new ResourceConfigEntry(null, null));
        list.add(new ResourceConfigEntry(1, null));
        list.add(new ResourceConfigEntry(-1, null));
        list.add(new ResourceConfigEntry(null, null));

        configList.sort(list);

        final List<ResourceConfigEntry> expected = new ArrayList();
        expected.add(new ResourceConfigEntry(19, null));
        expected.add(new ResourceConfigEntry(18, null));
        expected.add(new ResourceConfigEntry(17, null));
        expected.add(new ResourceConfigEntry(8, null));
        expected.add(new ResourceConfigEntry(3, null));
        expected.add(new ResourceConfigEntry(1, null));
        expected.add(new ResourceConfigEntry(-1, null));
        Assertions.assertEquals(expected, list);
    }

    @Nested
    class HandleEntryTests {
        @Test
        void noMatches() {

            // Arrange
            final ResourceConfigList configList = new StubbedList("stub", (c) -> {});

            final String entry = "@tree(minecraft:stone)";

            // Act
            final boolean result = configList.handleEntry(entry, 0);

            // Assert
            Assertions.assertFalse(result);
            //TODO Mockito.verify(ICBMClassic.logger()).error("{}: Unknown format for entry '{}'", "stub", entry);
            final Map<ResourceLocation, List<ResourceConfigEntry>> expected = new HashMap();
            Assertions.assertEquals(expected, configList.contentMatchers);
        }

        @Test
        void keyValueRegex() {

            // Arrange
            final Function fun = (v) -> true;
            final ResourceLocation key = new ResourceLocation("minecraft", "stone");

            final boolean[] wasCalled = {false};
            final ResourceConfigList configList = new StubbedList("stub", (c) -> {}) {
                //Work around as Mockito.spy is breaking code-coverage
                @Override
                protected Function getSimpleValue(ResourceLocation k, @Nullable Object o) {
                    wasCalled[0] = true;
                    Assertions.assertEquals(key, k);
                    Assertions.assertNull(o);
                    return fun;
                }
            };

            final String entry = "minecraft:stone";

            // Act
            final boolean result = configList.handleEntry(entry, 0);

            // Assert
            Assertions.assertTrue(wasCalled[0]);
            Assertions.assertTrue(result);
            final Map<ResourceLocation, List<ResourceConfigEntry>> expected = new HashMap();
            expected.put(key, Collections.singletonList(new ResourceConfigEntry(0, fun)));
            Assertions.assertEquals(expected, configList.contentMatchers);
        }

        @Test
        void keyValueRegex_withValue() {

            // Arrange
            final String entry = "minecraft:stone=578";
            final Function fun = (v) -> true;
            final ResourceLocation key = new ResourceLocation("minecraft", "stone");

            final boolean[] wasCalled = {false};
            final ResourceConfigList configList = new StubbedList("stub", (c) -> {}) {
                //Work around as Mockito.spy is breaking code-coverage
                @Override
                protected Function getSimpleValue(ResourceLocation k, @Nullable Object o) {
                    wasCalled[0] = true;
                    Assertions.assertEquals(key, k);
                    Assertions.assertEquals(578, o);
                    return fun;
                }

                @Override
                protected Object parseValue(@Nullable String value) {
                    assert value != null;
                    return Integer.parseInt(value);
                }
            };

            // Act
            final boolean result = configList.handleEntry(entry, 0);

            // Assert
            Assertions.assertTrue(wasCalled[0]);
            Assertions.assertTrue(result);
            final Map<ResourceLocation, List<ResourceConfigEntry>> expected = new HashMap();
            expected.put(key, Collections.singletonList(new ResourceConfigEntry(0, fun)));
            Assertions.assertEquals(expected, configList.contentMatchers);
        }

        @Test
        void keyValueRegex_rejected() {
            // Arrange
            final String entry = "minecraft:stone";
            final ResourceLocation key = new ResourceLocation("minecraft", "stone");

            final boolean[] wasCalled = {false};
            final ResourceConfigList configList = new StubbedList("stub", (c) -> {}) {
                @Override
                protected Function getSimpleValue(ResourceLocation k, @Nullable Object o) {
                    wasCalled[0] = true;
                    Assertions.assertEquals(key, k);
                    Assertions.assertNull(o);
                    return null; // null is rejected, usually means key is not found
                }
            };

            // Act
            final boolean result = configList.handleEntry(entry, 0);

            // Assert
            Assertions.assertTrue(wasCalled[0]);
            Assertions.assertFalse(result);
            final Map<ResourceLocation, List<ResourceConfigEntry>> expected = new HashMap();
            Assertions.assertEquals(expected, configList.contentMatchers);
        }

        @Test
        void metadataRegex() {
            // Arrange
            final String entry = "minecraft:stone@1";
            final Function fun = (v) -> true;
            final ResourceLocation key = new ResourceLocation("minecraft", "stone");

            final boolean[] wasCalled = {false};
            final ResourceConfigList configList = new StubbedList("stub", (c) -> {}) {
                @Override
                protected Function getMetaValue(ResourceLocation k, int metadata, @Nullable Object o) {
                    wasCalled[0] = true;
                    Assertions.assertEquals(key, k);
                    Assertions.assertEquals(1, metadata);
                    Assertions.assertNull(o);
                    return fun;
                }
            };

            // Act
            final boolean result = configList.handleEntry(entry, 0);

            // Assert
            Assertions.assertTrue(wasCalled[0]);
            Assertions.assertTrue(result);
            final Map<ResourceLocation, List<ResourceConfigEntry>> expected = new HashMap();
            expected.put(key, Collections.singletonList(new ResourceConfigEntry(0, fun)));

            Assertions.assertEquals(expected, configList.contentMatchers);
        }

        @Test
        void metadataRegex_withValue() {
            // Arrange
            final String entry = "minecraft:stone@1=10";
            final Function fun = (v) -> true;
            final ResourceLocation key = new ResourceLocation("minecraft", "stone");

            final boolean[] wasCalled = {false};
            final ResourceConfigList configList = new StubbedList("stub", (c) -> {}) {
                @Override
                protected Function getMetaValue(ResourceLocation k, int metadata, @Nullable Object o) {
                    wasCalled[0] = true;
                    Assertions.assertEquals(key, k);
                    Assertions.assertEquals(1, metadata);
                    Assertions.assertEquals(10, o);
                    return fun;
                }

                @Override
                protected Object parseValue(@Nullable String value) {
                    assert value != null;
                    return Integer.parseInt(value);
                }
            };

            // Act
            final boolean result = configList.handleEntry(entry, 0);

            // Assert
            Assertions.assertTrue(wasCalled[0]);
            Assertions.assertTrue(result);
            final Map<ResourceLocation, List<ResourceConfigEntry>> expected = new HashMap();
            expected.put(key, Collections.singletonList(new ResourceConfigEntry(0, fun)));

            Assertions.assertEquals(expected, configList.contentMatchers);
        }

        @Test
        void metadataRegex_rejected() {
            // Arrange
            final String entry = "minecraft:stone@1";
            final ResourceLocation key = new ResourceLocation("minecraft", "stone");

            final boolean[] wasCalled = {false};
            final ResourceConfigList configList = new StubbedList("stub", (c) -> {}) {
                @Override
                protected Function getMetaValue(ResourceLocation k, int metadata, @Nullable Object o) {
                    wasCalled[0] = true;
                    Assertions.assertEquals(key, k);
                    Assertions.assertEquals(1, metadata);
                    Assertions.assertNull(o);
                    return null;
                }
            };

            // Act
            final boolean result = configList.handleEntry(entry, 0);

            // Assert
            Assertions.assertTrue(wasCalled[0]);
            Assertions.assertFalse(result);
            final Map<ResourceLocation, List<ResourceConfigEntry>> expected = new HashMap();
            Assertions.assertEquals(expected, configList.contentMatchers);
        }

        @Test
        void sortingRegex() {
            // Arrange
            final String entry = "@sort(3,minecraft:stone)";
            final Function fun = (v) -> true;
            final ResourceLocation key = new ResourceLocation("minecraft", "stone");

            final boolean[] wasCalled = {false};
            final ResourceConfigList configList = new StubbedList("stub", (c) -> {}) {
                @Override
                protected Function getSimpleValue(ResourceLocation k, @Nullable Object o) {
                    wasCalled[0] = true;
                    Assertions.assertEquals(key, k);
                    Assertions.assertNull(o);
                    return fun;
                }
            };

            // Act
            final boolean result = configList.handleEntry(entry, 33);

            // Assert
            Assertions.assertTrue(wasCalled[0]);
            Assertions.assertTrue(result);

            final Map<ResourceLocation, List<ResourceConfigEntry>> expected = new HashMap();
            expected.put(key, Collections.singletonList(new ResourceConfigEntry(3, fun)));

            Assertions.assertEquals(expected, configList.contentMatchers);
        }

        @Test
        void domainRegex() {
            // Arrange
            final String entry = "@domain:minecraft";
            final Function fun = (v) -> true;

            final boolean[] wasCalled = {false};
            final ResourceConfigList configList = new StubbedList("stub", (c) -> {}) {
                @Override
                protected Function getDomainValue(String domain, @Nullable Object o) {
                    wasCalled[0] = true;
                    Assertions.assertEquals("minecraft", domain);
                    Assertions.assertNull(o);
                    return fun;
                }
            };

            // Act
            final boolean result = configList.handleEntry(entry, 3);

            // Assert
            Assertions.assertTrue(wasCalled[0]);
            Assertions.assertTrue(result);
            Assertions.assertEquals(Collections.singletonList(new ResourceConfigEntry(3, fun)), configList.generalMatchers);
        }

        @Test
        void domainRegex_rejected() {
            final String entry = "@domain:minecraft";

            final boolean[] wasCalled = {false};
            final ResourceConfigList configList = new StubbedList("stub", (c) -> {}) {
                @Override
                protected Function getDomainValue(String domain, @Nullable Object o) {
                    wasCalled[0] = true;
                    Assertions.assertEquals("minecraft", domain);
                    Assertions.assertNull(o);
                    return null; // null is rejected
                }
            };

            // Act
            final boolean result = configList.handleEntry(entry, 30);

            // Assert
            Assertions.assertTrue(wasCalled[0]);
            Assertions.assertFalse(result);
            Assertions.assertEquals(Collections.emptyList(), configList.generalMatchers);
        }
    }

    public static class StubbedList extends ResourceConfigList {

        public StubbedList(String name, Consumer reloadCallback) {
            super(name, "www.config.url", reloadCallback);
        }

        @Override
        protected ResourceLocation getContentKey(Object o) {
            return null;
        }

        @Override
        protected Function getDomainValue(String domain, @Nullable Object o) {
            return null;
        }

        @Override
        protected Function getSimpleValue(ResourceLocation key, @Nullable Object o) {
            return null;
        }

        @Override
        protected Function getMetaValue(ResourceLocation key, int metadata, @Nullable Object o) {
            return null;
        }

        @Override
        protected Object parseValue(@Nullable String value) {
            return null;
        }
    }
}