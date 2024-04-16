package icbm.classic.config.util;

import icbm.classic.ICBMClassic;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Prefab for anything that allows blacklist based on {@link ResourceLocation}
 * <p>
 * Life Cycle:
 * - unlock
 * - clear existing
 * - load(external inputs)
 * - batch(registry)
 * - lock
 */
@RequiredArgsConstructor
public abstract class ResourceConfigList<CONFIG extends ResourceConfigList, CONTENT> {
    // TODO once ContentBuilder Json system is published for MC replace this with JSON version to support programmatic and more complex entries
    //      entries to consider once JSON is allowed: time/date specific, conditional statements such as IF(MOD) IF(WORLD) IF(MATH) IF(GEO_AREA), block sets/lists

    // Constructor
    @Getter
    private final String name;
    private final Consumer<CONFIG> reloadCallback;

    // Block lists
    protected final Map<ResourceLocation, List<Function<CONTENT, Boolean>>> contentMatchers = new HashMap();
    protected final List<Function<CONTENT, Boolean>> generalMatchers = new ArrayList<>();

    // States
    @Getter
    private boolean isLocked = false;

    public void reload() {
        this.unlock();
        this.reset();
        // Let the parent load defaults and pull in configs
        this.reloadCallback.accept((CONFIG)this);
        this.processData();
        this.lock();
    }

    protected void processData(){

    }

    private void unlock() {
        this.isLocked = false;
    }

    private void reset() {
        contentMatchers.clear();
    }

    private void lock() {
        this.isLocked = true;
    }

    public boolean contains(CONTENT state) {
        if (state == null) {
            return false;
        }
        final ResourceLocation key = getContentKey(state);
        final List<Function<CONTENT, Boolean>> matchers = contentMatchers.get(key);
        if(matchers != null) {
            for(Function<CONTENT, Boolean> matcher: matchers) {
                if(matcher.apply(state)) {
                    matcherHit(state, matcher);
                    return true;
                }
            }
        }
        for(Function<CONTENT, Boolean> matcher: generalMatchers) {
            if(matcher.apply(state)) {
                matcherHit(state, matcher);
                return true;
            }
        }
        return false;
    }

    protected void matcherHit(CONTENT content, Function<CONTENT, Boolean> matcher) {

    }

    protected abstract ResourceLocation getContentKey(CONTENT content);

    /**
     * Loads block states from a collection of strings. Strings can contain nearly anything
     * so long as it results in block(s) or block-state(s).
     *
     * @param entries to process
     */
    public void load(Iterable<String> entries) {
        if (checkLock("entries", () -> String.join(", ", entries))) {
            return;
        }

        entries.forEach((str) -> {
            final String entry = str.trim();
            handleEntry(entry);
        });
    }

    /**
     * Loads block states from a collection of strings. Strings can contain nearly anything
     * so long as it results in block(s) or block-state(s).
     *
     * @param entries to process
     */
    public void load(String... entries) {
        if (checkLock("entries", () -> String.join(", ", entries))) { //TODO trim string to not dump 1000s of lines into logs
            return;
        }

        for (String str : entries) {
            final String entry = str.trim();
            handleEntry(entry);
        }
    }

    private boolean checkLock(String type, Supplier<String> entry) {
        if (this.isLocked) {
            ICBMClassic.logger().error(name + ": list is locked. Unable to add '" + type + "' entry '" + entry.get() + "'", new IllegalArgumentException());
            return true;
        }
        return false;
    }

    boolean handleEntry(String entry) {
        if(!entry.startsWith("@domain:")) {
            final String domain = entry.split(":")[1];
            this.generalMatchers.add((content -> getContentKey(content).getResourceDomain().equalsIgnoreCase(domain)));
            return true;
        }
        return handleSimpleBlock(entry);
    }

    boolean handleSimpleBlock(String entry) {
        final ResourceLocation blockKey = this.parseResourceLocation(entry);
        if(blockKey == null) {
            return false;
        }

        if(!ForgeRegistries.BLOCKS.containsKey(blockKey)) {
            ICBMClassic.logger().error(name + ": Failed to find block matching entry `" + entry + "`");
            return false;
        }

        this.contentMatchers
            .computeIfAbsent(blockKey, (k) -> new ArrayList<>())
            .add((content) -> getContentKey(content) == blockKey);

        return true;
    }

    ResourceLocation parseResourceLocation(String entry) {
        // TODO convert to regex
        final String[] keySplit = entry.split(":", -1);
        final String domain = keySplit.length == 2 ? keySplit[0].trim() : null;
        final String key = keySplit.length == 2 ? keySplit[1].trim() : null;
        if (keySplit.length != 2 || domain.isEmpty() || key.isEmpty()) {
            return null;
        }
        return new ResourceLocation(domain, key);
    }
}
