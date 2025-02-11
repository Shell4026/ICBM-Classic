package icbm.classic.api.reg.events;

import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.api.reg.obj.IBuilderRegistry;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired to allow registering action status message types
 */
public class ActionStatusRegistryEvent extends Event
{
    public final IBuilderRegistry<IActionStatus> registry;

    public ActionStatusRegistryEvent(IBuilderRegistry<IActionStatus> registry)
    {
        this.registry = registry;
    }
}
