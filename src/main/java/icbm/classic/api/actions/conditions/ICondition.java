package icbm.classic.api.actions.conditions;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.reg.obj.IBuildableObject;
import icbm.classic.api.reg.obj.IBuilderRegistry;

import javax.annotation.Nonnull;

/**
 * Generic system for applying conditionals for anything.
 */
public interface ICondition extends IBuildableObject {

    @Nonnull
    default IBuilderRegistry<ICondition> getRegistry() {
        return ICBMClassicAPI.TRIGGER_REGISTRY;
    }

    /** Called each tick */
    default void onTick()
    {
    }

    /**
     * Checks if the requirements are met for the condition to be considered passable.
     *
     * Do not assume if true it will always be true. As conditions may change between ticks or dependent variables
     * may no longer validate. It is up to the condition to decide if it is one-off validation or continuous. This
     * behavior is abstracted to avoid assumings in caching results.
     *
     * @return true if requirements are met
     */
    boolean isConditionMet();
}
