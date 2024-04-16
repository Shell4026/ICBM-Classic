package icbm.classic.config.util;

import icbm.classic.ICBMClassic;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public abstract class ResourceConfigList<CONFIG extends ResourceConfigList, CONTENT, VALUE> {
    // TODO once ContentBuilder Json system is published for MC replace this with JSON version to support programmatic and more complex entries
    //      entries to consider once JSON is allowed: time/date specific, conditional statements such as IF(MOD) IF(WORLD) IF(MATH) IF(GEO_AREA), block sets/lists

    protected final Pattern KEY_VALUE_REGEX = Pattern.compile( "^(.*):([^=\\s]*)(=(.*))?");
    protected final Pattern DOMAIN_VALE_REGEX = Pattern.compile("^@domain:(.*?)(=(.*))?$");


    // Constructor
    @Getter
    private final String name;
    private final Consumer<CONFIG> reloadCallback;

    // Block lists
    protected final Map<ResourceLocation, List<Function<CONTENT, VALUE>>> contentMatchers = new HashMap();
    protected final List<Function<CONTENT, VALUE>> generalMatchers = new ArrayList<>();

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

    public void set(ResourceLocation key, VALUE value) {
        contentMatchers
                .computeIfAbsent(key, k -> new ArrayList<>())
                .add(getSimpleValue(key, value));
    }

    public VALUE getValue(CONTENT state) {
        if (state == null) {
            return null;
        }
        final ResourceLocation key = getContentKey(state);
        final List<Function<CONTENT, VALUE>> matchers = contentMatchers.get(key);
        if(matchers != null) {
            for(Function<CONTENT, VALUE> matcher: matchers) {
                final VALUE result = matcher.apply(state);
                if(result != null) {
                    matcherHit(state, matcher);
                    return result;
                }
            }
        }
        for(Function<CONTENT, VALUE> matcher: generalMatchers) {
            final VALUE result = matcher.apply(state);
            if(result != null) {
                matcherHit(state, matcher);
                return result;
            }
        }
        return null;
    }

    protected void matcherHit(CONTENT content, Function<CONTENT, VALUE> matcher) {

    }

    protected abstract ResourceLocation getContentKey(CONTENT content);

    /**
     * Loads entries converting them into key:value functions
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
     * Loads entries converting them into key:value functions
     *
     * @param entries to process
     */
    public void load(String... entries) {
        if (checkLock("entries", () -> String.join(", ", entries))) { //TODO trim string to not dump 1000s of lines into logs
            return;
        }

        for (String str : entries) {
            final String entry = str.replaceAll("\\s", ""); //Kill spaces off -> "M I N E C R A F T" turns into "MINECRAFT"
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

        final Matcher matcher = DOMAIN_VALE_REGEX.matcher(entry);
        if (matcher.matches()) {
            final String domain = matcher.group(1);
            final String value = matcher.group(3);
            this.generalMatchers.add(getDomainValue(domain, parseValue(value)));
            return true;
        }
        return handleSimple(entry);
    }

    protected abstract Function<CONTENT, VALUE> getDomainValue(String domain, @Nullable VALUE value);

    protected abstract Function<CONTENT, VALUE> getSimpleValue(ResourceLocation key, @Nullable VALUE value);

    protected abstract VALUE parseValue(@Nullable String value);

    boolean handleSimple(String entry) {
        final Matcher matcher = KEY_VALUE_REGEX.matcher(entry);
        if (matcher.matches()) {
            final String domain = matcher.group(1);
            final String resource = matcher.group(2);
            if (matcher.groupCount() == 4) {
                this.contentMatchers
                    .computeIfAbsent(new ResourceLocation(domain, resource), (ResourceLocation k) -> new ArrayList<>())
                    .add(getSimpleValue(new ResourceLocation(domain, resource), parseValue(matcher.group(4))));
            } else {
                this.contentMatchers
                    .computeIfAbsent(new ResourceLocation(domain, resource), (ResourceLocation k) -> new ArrayList<>())
                    .add(getSimpleValue(new ResourceLocation(domain, resource), null));
            }
            return true;
        }
        return false;
    }
}
