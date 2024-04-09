package icbm.classic.api.data.meta;

import javax.annotation.Nonnull;

/**
 * Applied to data objects that can be filtered by meta tags
 */
public interface ITypeTaggable {

    MetaTag[] NO_TAGS = new MetaTag[0];

    /**
     * Type(s) to apply to this data object
     *
     * @return type(s)
     */
    @Nonnull
    default MetaTag[] getTypes() {
        return NO_TAGS;
    }

    /**
     * Checks if the data object applies for the given type
     *
     * @param type to check
     * @return true if is valid
     */
    default boolean isType(MetaTag type) {
        for(MetaTag projectileType : getTypes()) {
            if(projectileType.isAssignable(type)) {
                return true;
            }
        }
        return false;
    }
}
