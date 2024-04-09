package icbm.classic.content.actions.listners;

import icbm.classic.api.actions.IAction;
import icbm.classic.api.actions.status.IActionStatus;

import javax.annotation.Nullable;

@FunctionalInterface
public interface IActionListener {

    /**
     * Called when action is created but not yet run
     *
     * @param action created
     * @return null to do nothing, status to block the action and note why
     */
    @Nullable
    IActionStatus onAction(IAction action);
}
