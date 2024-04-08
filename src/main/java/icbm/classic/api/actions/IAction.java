package icbm.classic.api.actions;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.api.reg.obj.IBuildableObject;
import icbm.classic.api.reg.obj.IBuilderRegistry;

import javax.annotation.Nonnull;

/**
 * Generic action system allowing for abstract triggering of behaviors
 *
 * Example: triggering a blast, this would be an action that fall inside world-editing category
 */
public interface IAction extends IBuildableObject {

    /**
     * Executes the action and returns the status of the action.
     *
     * Status will indicate if the action was completed. If the action
     * has conditions that are not completed it will return as blocking {@link IActionStatus#isBlocking()}.
     *
     * Depending on the action not all conditions are required to be met.
     * This will be the case for missiles where impact may still trigger
     * the warhead's action(blast).
     *
     * @return the status of the action
     */
    IActionStatus doAction();


    @Nonnull
    @Override
    default IBuilderRegistry<IAction> getRegistry() {
        return ICBMClassicAPI.ACTION_REGISTRY;
    }
}
