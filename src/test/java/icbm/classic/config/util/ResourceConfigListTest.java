package icbm.classic.config.util;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

class ResourceConfigListTest {

    public class MockConfig extends ResourceConfigList<MockConfig, ResourceLocation, Integer> {
        public MockConfig(String name, Consumer reloadCallback) {
            super(name, reloadCallback);
        }

        @Override
        protected ResourceLocation getContentKey(ResourceLocation s) {
            return s;
        }

        @Override
        protected Function<ResourceLocation, Integer> getDomainValue(String domain, @Nullable Integer integer) {
            return (s) -> s.getResourceDomain().equalsIgnoreCase(domain) ? integer : null;
        }

        @Override
        protected Function<ResourceLocation, Integer> getSimpleValue(ResourceLocation key, @Nullable Integer integer) {
            return (s) -> s == key ? integer : null;
        }

        @Override
        protected Integer parseValue(@Nullable String value) {
            return Integer.parseInt(value);
        }
    }

}