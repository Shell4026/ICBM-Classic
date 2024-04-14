package icbm.classic.api.actions.listener;

import icbm.classic.api.actions.IAction;
import icbm.classic.api.actions.status.IActionStatus;

import javax.annotation.Nullable;

@FunctionalInterface
public interface IActionListener {

    /**
     * Called when action is created but not yet run. This allows for
     * blocking the action, modifying the action, or replacing the action.
     *
     * To block an action return a status with a non-complete status. Such as
     * error to indicate the action can't be run, or blocking to say it doesn't
     * meet some condition but could eventually run.
     *
     * Modifying is not recommended due to lack of visibility into the edit. As well
     * chance that something upstream needed that data. If desired action can be
     * instanceof checked or updated via the field provider system.
     *
     * Better option is to replace the action. This can be done by returning a status
     * that notes the replacement. Then firing a new action off back into the listener
     * system. Ensuring that this listener doesn't spawn another action from the same
     * one generated.
     *
     * @param action created, can be modified if needed
     *
     * @return null to do nothing, status to block the action and note why
     */
    @Nullable
    IActionStatus onAction(IAction action);
}
