package icbm.classic.api.actions;

import icbm.classic.api.data.meta.MetaTag;

import javax.annotation.Nullable;

/**
 *  Provides a generic way to get action potentials based on meta tag
 */
public interface IActionProvider {

    /**
     * Gets the potential action based on the tag
     *
     * Example:  {@link icbm.classic.api.actions.data.ActionTypes#PROJECTILE} would
     * return an action for spawning projectile entity.
     *
     * {@link icbm.classic.api.actions.data.ActionTypes#ENTITY_CREATION} might return
     * the same value if the provider has no other options.
     *
     * @param key to match to the action
     * @return potential action
     */
    @Nullable
    IPotentialAction getPotentialAction(MetaTag key);
}
