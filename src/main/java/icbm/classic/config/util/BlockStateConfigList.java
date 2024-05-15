package icbm.classic.config.util;

import com.google.common.collect.ImmutableMap;
import icbm.classic.ICBMClassic;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * List of BlockStates/Blocks to use in config purposes. This is meant for internal use by the mod and should
 * never be touched by other mods. Use events, integrations, or ask for changes before bypassing this system.
 */
public abstract class BlockStateConfigList<VALUE> extends ResourceConfigList<BlockStateConfigList<VALUE>, IBlockState, VALUE> {



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
            if(getContentKey(blockState).getResourceDomain().equalsIgnoreCase(domain)) {
                return value;
            }
            return null;
        };
    }

    @Override
    protected Function<IBlockState, VALUE> getSimpleValue(ResourceLocation key, @Nullable VALUE value) {
        return (blockState) -> {
            if(getContentKey(blockState) == key) {
                return value;
            }
            return null;
        };
    }

    @Override
    protected Function<IBlockState, VALUE> getMetaValue(ResourceLocation key, int metadata, @Nullable VALUE value) {
        return (blockState) -> {
            if(getContentKey(blockState) == key && blockState.getBlock().getMetaFromState(blockState) == metadata) {
                return value;
            }
            return null;
        };
    }

    @Override
    boolean handleEntry(String entryRaw, Integer index) {
        //TODO handle properties
        //TODO handle fuzzy ~
        return super.handleEntry(entryRaw, index);
    }


    protected IBlockState parseBlockState(String entry) {
        final Matcher metaMatcher = META_KEY_REGEX.matcher(entry);
        if(metaMatcher.matches()) {
            final String domain = metaMatcher.group(1);
            final String resource = metaMatcher.group(2);
            final int meta = Integer.parseInt(metaMatcher.group(3));

            final ResourceLocation key = new ResourceLocation(domain, resource);
            final Block block = ForgeRegistries.BLOCKS.getValue(key);
            if(block != null) {
                return block.getStateFromMeta(meta);
            }
        }

        final Matcher matcher = KEY_VALUE_REGEX.matcher(entry);
        if (matcher.matches()) {
            final String domain = matcher.group(1);
            final String resource = matcher.group(2);

            final ResourceLocation key = new ResourceLocation(domain, resource);
            final Block block = ForgeRegistries.BLOCKS.getValue(key);
            if(block != null) {
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
