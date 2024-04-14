package icbm.classic.lib.actions.listners;

import icbm.classic.api.actions.IAction;
import icbm.classic.api.actions.IActionData;
import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.api.data.meta.MetaTag;
import lombok.NoArgsConstructor;

/**
 * Handles firing events for actions using {@link IActionData#getTypes()} as the event channels
 */
@NoArgsConstructor()
public class ActionListenerSystem {
    //TODO implement, rather than firing on forge bus this class will handle all events for actions
    //TODO add to API exposed via ICBMClassicAPI using interfaces only.. though we could consider an annotation but that sounds annoying. Other option would be module thing... forgot the name but we use it in the content system but would need an annotation processor to feel nice

    private final ActionListenerLayer root = new ActionListenerLayer(null);

    public void addListener(IActionListener listener, MetaTag... tags) {
        for (MetaTag tag : tags) {
            root.add(listener, tag);
        }
    }

    public IActionStatus runAction(IAction action) {
        // TODO feed through listeners using the action data tags
        return action.doAction();
    }
}
