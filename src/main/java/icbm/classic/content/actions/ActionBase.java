package icbm.classic.content.actions;

import icbm.classic.api.actions.IAction;
import icbm.classic.api.actions.IActionData;
import icbm.classic.api.actions.cause.IActionSource;
import lombok.Data;

@Data
public abstract class ActionBase implements IAction {

    private final IActionSource source;
    private final IActionData actionData;
}
