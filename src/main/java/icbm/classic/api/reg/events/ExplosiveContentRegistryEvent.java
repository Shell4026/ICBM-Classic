package icbm.classic.api.reg.events;

import icbm.classic.api.reg.IExplosiveRegistry;
import icbm.classic.api.reg.content.IExplosiveContentRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

/**
 * Used to register new content types before explosives are registered.
 * <p>
 * Created by Dark(DarkGuardsman, Robin) on 1/4/19.
 * @deprecated being removed in 1.13
 */
@Deprecated
public class ExplosiveContentRegistryEvent extends Event
{

    public final IExplosiveRegistry registry;

    public ExplosiveContentRegistryEvent(IExplosiveRegistry registry)
    {
        this.registry = registry;
    }

    /**
     * Called to register a new content type
     *
     * @param id              - unique name of the content type
     * @param contentRegistry - content type
     */
    public void register(ResourceLocation id, IExplosiveContentRegistry contentRegistry)
    {
        registry.registerContentRegistry(id, contentRegistry);
    }
}
