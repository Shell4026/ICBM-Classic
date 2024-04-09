package icbm.classic.api.actions;

import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.api.actions.cause.IActionSource;

import javax.annotation.Nonnull;

/**
 * Generic action system allowing for abstract logic as reusable behaviors. Will include
 * data to describe what the action is and source for how it triggered.
 *
 * Avoid caching actions outside a source. As the action may be recycled or cached by the source itself. This will
 * especially be the case for blasts that may fire 1000s of actions for event purposes.
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
     * When running action make sure to push through event listeners. Allowing external systems to intercept
     * and validate if action is good to complete. This will especially be useful in cases of world editing.
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
