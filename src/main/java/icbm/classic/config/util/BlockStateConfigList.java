package icbm.classic.config.util;

import com.google.common.collect.ImmutableMap;
import icbm.classic.ICBMClassic;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * List of BlockStates/Blocks to use in config purposes. This is meant for internal use by the mod and should
 * never be touched by other mods. Use events, integrations, or ask for changes before bypassing this system.
 */
public abstract class BlockStateConfigList<VALUE> extends ResourceConfigList<BlockStateConfigList<VALUE>, IBlockState, VALUE> {

    protected static final Pattern BLOCK_PROP_REGEX = Pattern.compile("^(.*):([^=\\s]*)\\[((?:[\\w.]+:[\\w.]+[,]?)+)\\](?:=(.*))?");


    public BlockStateConfigList(String name, Consumer<BlockStateConfigList<VALUE>> reloadCallback) {
        super(name, reloadCallback);
    }

    @Override
    protected ResourceLocation getContentKey(IBlockState iBlockState) {
        return iBlockState.getBlock().getRegistryName();
    }

    @Override
    protected Function<IBlockState, VALUE> getDomainValue(String domain, @Nullable VALUE value) {
        return (blockState) -> {
            if (getContentKey(blockState).getResourceDomain().equalsIgnoreCase(domain)) {
                return value;
            }
            return null;
        };
    }

    @Override
    protected Function<IBlockState, VALUE> getSimpleValue(ResourceLocation key, @Nullable VALUE value) {
        return (blockState) -> {
            if (getContentKey(blockState) == key) {
                return value;
            }
            return null;
        };
    }

    @Override
    protected Function<IBlockState, VALUE> getMetaValue(ResourceLocation key, int metadata, @Nullable VALUE value) {
        return (blockState) -> {
            if (getContentKey(blockState) == key && blockState.getBlock().getMetaFromState(blockState) == metadata) {
                return value;
            }
            return null;
        };
    }

    protected Function<IBlockState, VALUE> getPropValue(ResourceLocation key, Map<IProperty, Function<Comparable, Boolean>> propMatchers, @Nullable VALUE value) {
        return (blockState) -> {
            if (getContentKey(blockState) == key) {
                final ImmutableMap<IProperty<?>, Comparable<?>> stateProps = blockState.getProperties();
                for(IProperty propKey: propMatchers.keySet()) {
                    if(!stateProps.containsKey(propKey)) {
                        return null;
                    }

                    final Function<Comparable, Boolean> check = propMatchers.get(propKey);
                    if(check != null && !check.apply(stateProps.get(propKey))) {
                        return null;
                    }
                }
                return value;
            }
            return null;
        };
    }

    @Override
    boolean handleEntry(String entryRaw, Integer index) {

        final Matcher domainMatcher = BLOCK_PROP_REGEX.matcher(entryRaw);
        if (domainMatcher.matches()) {
            final String domain = domainMatcher.group(1);
            final String resource = domainMatcher.group(2);
            final ResourceLocation key = new ResourceLocation(domain, resource);

            final Block block = ForgeRegistries.BLOCKS.getValue(key);
            if (block == null) {
                // TODO error log
                return false;
            }

            final String[] props = domainMatcher.group(3).split(",");
            final Map<IProperty, Function<Comparable, Boolean>> propMatchers = collectBlockProps(block, props);
            if (propMatchers == null) {
                return false;
            }

            final String value = domainMatcher.group(4);
            this.generalMatchers.add(new ResourceConfigEntry<>(index, getPropValue(key, propMatchers, parseValue(value))));
            return true;
        }

        //TODO handle fuzzy ~
        return super.handleEntry(entryRaw, index);
    }

    protected Map<IProperty, Function<Comparable, Boolean>> collectBlockProps(Block block, String[] props) {
        final Map<IProperty, Function<Comparable, Boolean>> matchers = new HashMap();
        for (String propEntry : props) {
            final Matcher propMatcher = KEY_VALUE_REGEX.matcher(propEntry);
            if (!propMatcher.matches()) {
                // TODO error log
                return null;
            }
            final String propKey = propMatcher.group(1);
            final String propValue = propMatcher.group(2);
            //TODO consider using group 3 as a boolean contains check

            final IProperty property = block.getBlockState().getProperty(propKey);
            if (property == null) {
                ICBMClassic.logger().error("{}: Failed to find property '{}' for block '{}' matching entry `{}`",
                    this.getName(), propKey, block.getRegistryName(), propEntry);
                return null;
            }

            if (propValue.equals("~")) {
                matchers.put(property, (o) -> true);
            } else if (propValue.startsWith("~") || propValue.endsWith("~")) {
                final String stringMatch = propValue.substring(1).trim();
                final List<Comparable<?>> valuesToMatch = (List<Comparable<?>>) property.getAllowedValues().stream()
                    .filter(o -> {
                        if (propValue.endsWith("~")) {
                            return property.getName((Comparable) o).endsWith(stringMatch);
                        }
                        return property.getName((Comparable) o).startsWith(stringMatch);
                    }).collect(Collectors.toList());
                if (valuesToMatch.isEmpty()) {
                    ICBMClassic.logger().error("Config Flying Block: Failed to find values matching '{}' for property '{}' and block '{}' matching entry '{}'",
                        propValue, propKey, block.getRegistryName(), propEntry);
                    return null;
                }
                matchers.put(property, valuesToMatch::contains);
            } else {
                // Simple value matcher
                final Optional value = property.getAllowedValues().stream().filter(o -> property.getName((Comparable) o).equalsIgnoreCase(propValue)).findFirst();
                if (!value.isPresent()) {
                    ICBMClassic.logger().error("Config Flying Block: Failed to find value matching '{}' for property '{}' and block '{}' matching entry '{}'",
                        propValue, propKey, block.getRegistryName(), propEntry);
                    return null;
                }
                matchers.put(property, (o) -> Objects.equals(value.get(), o));
            }
        }
        return matchers;
    }


    protected IBlockState parseBlockState(String entry) {
        final Matcher metaMatcher = META_KEY_REGEX.matcher(entry);
        if (metaMatcher.matches()) {
            final String domain = metaMatcher.group(1);
            final String resource = metaMatcher.group(2);
            final int meta = Integer.parseInt(metaMatcher.group(3));

            final ResourceLocation key = new ResourceLocation(domain, resource);
            final Block block = ForgeRegistries.BLOCKS.getValue(key);
            if (block != null) {
                return block.getStateFromMeta(meta);
            }
        }

        final Matcher matcher = KEY_VALUE_REGEX.matcher(entry);
        if (matcher.matches()) {
            final String domain = matcher.group(1);
            final String resource = matcher.group(2);

            final ResourceLocation key = new ResourceLocation(domain, resource);
            final Block block = ForgeRegistries.BLOCKS.getValue(key);
            if (block != null) {
                return block.getDefaultState();
            }
        }
        return null;
    }

    public static class BlockOut extends BlockStateConfigList<IBlockState> {

        public BlockOut(String name, Consumer<BlockStateConfigList<IBlockState>> reloadCallback) {
            super(name, reloadCallback);
        }

        @Override
        protected IBlockState parseValue(@Nullable String value) {
            return super.parseBlockState(value);
        }
    }

    public static class BlockChanceOut extends BlockStateConfigList<Pair<IBlockState, Float>> {

        public BlockChanceOut(String name, Consumer<BlockStateConfigList<Pair<IBlockState, Float>>> reloadCallback) {
            super(name, reloadCallback);
        }

        @Override
        protected Pair<IBlockState, Float> parseValue(@Nullable String value) {
            if (value == null) {
                return null;
            }
            if (value.contains(",")) { //TODO use regex to ensure we only have 1 comma
                final String[] split = value.split(",");
                return Pair.of(super.parseBlockState(split[0]), Math.min(1, Math.max(0, Float.parseFloat(split[1]))));
            }
            return Pair.of(super.parseBlockState(value), null);
        }
    }

    public static class FloatOut extends BlockStateConfigList<Float> {

        public FloatOut(String name, Consumer<BlockStateConfigList<Float>> reloadCallback) {
            super(name, reloadCallback);
        }

        @Override
        protected Float parseValue(@Nullable String value) {
            return value == null ? null : Float.parseFloat(value);
        }
    }
}
