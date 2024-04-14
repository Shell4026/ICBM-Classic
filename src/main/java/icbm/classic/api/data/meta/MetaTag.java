package icbm.classic.api.data.meta;

import lombok.Data;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * General purpose tags for use in specifying metadata about an object.
 * <p>
 * Used with {@link icbm.classic.api.actions.IActionData} to describe type of action and
 * {@link icbm.classic.api.missiles.projectile.IProjectileData} to describe type of projectile
 */
@Data
public final class MetaTag {

    /**
     * Unique id of the tag, is not a translation key
     */
    @Nonnull
    private final ResourceLocation id;
    /**
     * Parent of this tag
     */
    private final MetaTag parent;

    private NonNullList<MetaTag> children;

    private MetaTag(@Nonnull ResourceLocation id, MetaTag parent) {
        this.id = id;
        this.parent = parent;
    }

    public static MetaTag create(ResourceLocation resourceLocation) {
        return create(null, resourceLocation);
    }

    public static MetaTag create(MetaTag parent, String subtype) {
        return create(parent, new ResourceLocation(parent.id.getResourceDomain(), parent.id.getResourcePath() + "." + subtype));
    }

    public static MetaTag create(MetaTag parent, ResourceLocation resourceLocation) {
        final MetaTag metaTag = new MetaTag(resourceLocation, parent);
        if (parent != null) {
            parent.add(metaTag);
        }
        return metaTag;
    }

    private void add(MetaTag tag) {
        if (this.children == null) {
            this.children = NonNullList.create();
        }
        this.children.add(tag);
    }

    /**
     * Checks if the tag is a direct child of this tag. Doesn't check children's children
     *
     * @param tag to check
     * @return true if is a direct child
     */
    public boolean isChild(MetaTag tag) {
        return children != null && children.contains(tag);
    }

    /**
     * Checks if the type is a subtype of this tag. Will check children using the same method. Expect O(N) performance
     * where N is the SUM of all subtypes of this tag.
     *
     * @param type to check
     * @return true if is subtype, excluding self
     */
    public boolean isSubType(MetaTag type) {
        // TODO if this is being called often and performance isn't great, cache result
        return isChild(type) || children != null && children.stream().anyMatch((c) -> c.isSubType(type));
    }

    /**
     * <pre>
     * Checks if this tag or it's parent can be applied to the type
     *
     * Works similar to field assignment.
     *
     * Example:
     *  TagA exists with no parent
     *  TagB exists with TagA parent
     *
     *  TagA is assignable to TagA
     *  TagB is assignable to TagA
     *  TagB is not assignable to TagA
     * </pre>
     *
     * @param type to validate
     * @return true if this tag or parent would validate as matching
     */
    public boolean isAssignable(MetaTag type) {
        return parent != null && parent.isAssignable(type) || type == this;
    }

    public MetaTag getTopParent() {
        return this.getTopParent(null);
    }

    public MetaTag getTopParent(MetaTag stop) {
        if (this.parent == null || stop != null && this.parent == stop) {
            return this;
        }
        return this.parent.getTopParent(stop);
    }
}
