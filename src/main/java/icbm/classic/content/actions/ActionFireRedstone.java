package icbm.classic.content.actions;

import icbm.classic.api.actions.IAction;
import icbm.classic.api.actions.IActionData;
import icbm.classic.api.actions.cause.IActionSource;
import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.content.actions.status.ActionResponses;

import javax.annotation.Nonnull;

public class ActionFireRedstone extends ActionBase implements IAction {
    public ActionFireRedstone(IActionSource source, IActionData actionData) {
        super(source, actionData);
    }

    @Nonnull
    @Override
    public IActionStatus doAction() {
        return ActionResponses.COMPLETED;
    }
}
