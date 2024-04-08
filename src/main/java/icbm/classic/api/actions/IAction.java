package icbm.classic.api.actions;

import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.api.actions.cause.IActionSource;

import javax.annotation.Nonnull;

/**
 * Generic action system allowing for abstract triggering of behaviors
 *
 * Example: triggering a blast, this would be an action that fall inside world-editing category
 */
public interface IAction {

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
     * Actions should always be instant. If something needs to run over several ticks then it should
     * spawn a thread, listener, entity, or other system. This way actions can stay simple and not
     * require callbacks. If a callback is still needed... consider a status obj that provides access to
     * what was created.
     *
     * @return the status of the action
     */
    @Nonnull
    IActionStatus doAction();

    /**
     * Source of the action
     *
     * @return source
     */
    @Nonnull
    IActionSource getSource();

    /**
     * Gets data used to create this action
     *
     * @return data
     */
    @Nonnull
    IActionData getActionData();
}
