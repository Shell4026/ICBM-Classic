package icbm.classic.config.util;

import akka.japi.pf.Match;
import icbm.classic.ICBMClassic;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * <p>
 * Documentation <a href="https://github.com/BuiltBrokenModding/ICBM-Classic/wiki/config-resource-list">Resource List Config</a>
 */
@RequiredArgsConstructor
public abstract class ResourceConfigList<CONFIG extends ResourceConfigList, CONTENT, VALUE> {
    // TODO once ContentBuilder Json system is published for MC replace this with JSON version to support programmatic and more complex entries
    //      entries to consider once JSON is allowed: time/date specific, conditional statements such as IF(MOD) IF(WORLD) IF(MATH) IF(GEO_AREA), block sets/lists

    protected static final Pattern SORTING_REGEX = Pattern.compile("^@sort\\((\\d*),(\\S*)\\)$");
    protected static final Pattern DOMAIN_VALE_REGEX = Pattern.compile("^@domain:([^=]*)(?:=(\\S))?$");

    protected static final Pattern KEY_VALUE_REGEX = Pattern.compile("^([^\\s@]*):([^\\s=]*)(?:=(\\S*))?");
    protected static final Pattern META_KEY_REGEX = Pattern.compile("^([^\\s@]*):([^\\s=@]*)@([0-9]+)(?:=(\\S*))?");

    // Constructor
    @Getter
    private final String name;
    private final String configUrl; //TODO load from a JSON localization metadata file for better dev configuration and traceability of URL usage
    private final Consumer<CONFIG> reloadCallback;

    // Block lists
    protected final Map<ResourceLocation, List<ResourceConfigEntry<CONTENT, VALUE>>> contentMatchers = new HashMap();
    protected final Map<ResourceLocation, List<ResourceConfigEntry<CONTENT, VALUE>>> defaultMatchers = new HashMap();
    protected final List<ResourceConfigEntry<CONTENT, VALUE>> generalMatchers = new ArrayList<>();

    // States
    @Getter
    private boolean isLocked = false;

    public void reload() {
        this.unlock();
        this.reset();
        // Let the parent load defaults and pull in configs
        this.reloadCallback.accept((CONFIG) this);
        this.processData();
        // Sort functions
        sort(generalMatchers);
        contentMatchers.values().forEach(this::sort);
        defaultMatchers.values().forEach(this::sort);
        this.lock();
    }

    protected void sort(List<ResourceConfigEntry<CONTENT, VALUE>> list) {
        // Generate defaults
        int highestUserSort = list.stream().map(ResourceConfigEntry::getOrder).mapToInt(i -> i == null ? 0 : i).max().orElse(0);

        int index = 0;
        for (ResourceConfigEntry<CONTENT, VALUE> func : list) {
            if (func.getOrder() == null) {
                index++;
                func.setOrder(highestUserSort + index);
            }
        }

        // Do sort
        list.sort(this::compare);
    }

    protected int compare(ResourceConfigEntry<CONTENT, VALUE> a, ResourceConfigEntry<CONTENT, VALUE> b) {
        int sortA = a.getOrder();
        int sortB = b.getOrder();
        return sortB - sortA;
    }

    protected void processData() {

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

    public void setDefault(ResourceLocation key, VALUE value, int order) {
        defaultMatchers
            .computeIfAbsent(key, k -> new ArrayList<>())
            .add(new ResourceConfigEntry<>(order, getSimpleValue(key, value)));
    }

    public VALUE getValue(CONTENT state) {
        if (state == null) {
            return null;
        }
        final ResourceLocation key = getContentKey(state);

        final VALUE contentValue = getValue(state, contentMatchers.get(key));
        if (contentValue != null) return contentValue;

        for (Function<CONTENT, VALUE> matcher : generalMatchers) {
            final VALUE result = matcher.apply(state);
            if (result != null) {
                matcherHit(state, matcher);
                return result;
            }
        }

        return getValue(state, this.defaultMatchers.get(key));
    }

    private VALUE getValue(CONTENT state, List<ResourceConfigEntry<CONTENT, VALUE>> matchers) {
        if (matchers != null) {
            for (Function<CONTENT, VALUE> matcher : matchers) {
                final VALUE result = matcher.apply(state);
                if (result != null) {
                    matcherHit(state, matcher);
                    return result;
                }
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
    public void load(String... entries) {
        if (checkLock("entries", () -> String.join(", ", entries))) { //TODO trim string to not dump 1000s of lines into logs
            return;
        }

        boolean someFailed = false;
        for (String str : entries) {
            someFailed = someFailed | handleEntry(str, null);
        }

        if(someFailed) {
            ICBMClassic.logger().error("{}: Some entries failed to process. Check config and verify usage with documentation at {}", this.getName(), this.configUrl);
        }
    }

    private boolean checkLock(String type, Supplier<String> entry) {
        if (this.isLocked) {
            ICBMClassic.logger().error("{}: list is locked. Unable to add '{}' entry '{}'", name, type, entry.get(), new IllegalArgumentException());
            return true;
        }
        return false;
    }

    protected boolean handleEntry(String entryRaw, Integer index) {

        final String entry = entryRaw.replaceAll("\\s", ""); //Kill spaces off -> "M I N E C R A F T" turns into "MINECRAFT"

        // TODO allow permutation arguments
        final Matcher sortMatcher = SORTING_REGEX.matcher(entry);
        if (sortMatcher.matches()) {
            return handleEntry(sortMatcher.group(2), Integer.parseInt(sortMatcher.group(1)));
        }

        final Matcher domainMatcher = DOMAIN_VALE_REGEX.matcher(entry);
        if (domainMatcher.matches()) {
            final String domain = domainMatcher.group(1);

            final String valueStr = domainMatcher.group(2);
            final VALUE value = parseValue(valueStr);

            final Function<CONTENT, VALUE> matcher = getDomainValue(domain, value);
            if(matcher == null) {
                return false;
            }

            this.generalMatchers.add(new ResourceConfigEntry<>(index, matcher));
            return true;
        }

        final Matcher metaMatcher = META_KEY_REGEX.matcher(entry);
        if(metaMatcher.matches()) {
            final String domain = metaMatcher.group(1);
            final String resource = metaMatcher.group(2);
            final ResourceLocation key = new ResourceLocation(domain, resource);

            final int meta = Integer.parseInt(metaMatcher.group(3));

            final String valueStr = metaMatcher.group(4);
            final VALUE value = parseValue(valueStr);

            final Function<CONTENT, VALUE> matcher = getMetaValue(key, meta, value);
            if(matcher == null) {
                return false;
            }

            this.contentMatchers
                .computeIfAbsent(key, (ResourceLocation k) -> new ArrayList<>())
                .add(new ResourceConfigEntry<>(index, matcher));
            return true;
        }
        return handleSimple(entry, index);
    }

    protected abstract Function<CONTENT, VALUE> getDomainValue(String domain, @Nullable VALUE value);

    protected abstract Function<CONTENT, VALUE> getSimpleValue(ResourceLocation key, @Nullable VALUE value);

    protected abstract Function<CONTENT, VALUE> getMetaValue(ResourceLocation key, int metadata, @Nullable VALUE value);

    protected abstract VALUE parseValue(@Nullable String value);

    boolean handleSimple(String entry, Integer index) {
        final Matcher regexMatcher = KEY_VALUE_REGEX.matcher(entry);
        if (regexMatcher.matches()) {
            final String domain = regexMatcher.group(1);
            final String resource = regexMatcher.group(2);
            final ResourceLocation key = new ResourceLocation(domain, resource);

            final VALUE value = parseValue(regexMatcher.group(3));
            final Function<CONTENT, VALUE> matcher = getSimpleValue(key, value);
            if(matcher == null) {
                return false;
            }

            this.contentMatchers
                .computeIfAbsent(key, (ResourceLocation k) -> new ArrayList<>())
                .add(new ResourceConfigEntry<>(index, matcher));
            return true;
        }

        ICBMClassic.logger().error("{}: Unknown format for entry '{}'", this.getName(), entry);
        return false;
    }
}
