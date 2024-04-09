package icbm.classic.api.actions;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.actions.cause.IActionSource;
import icbm.classic.api.data.meta.ITypeTaggable;
import icbm.classic.api.reg.obj.IBuildableObject;
import icbm.classic.api.reg.obj.IBuilderRegistry;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Registry data used to create actions in a world position
 */
public interface IActionData extends IBuildableObject, ITypeTaggable {

    /**
     * Creates a new action
     *
     * @param world to create action in
     * @param x location
     * @param y location
     * @param z location
     * @param source of the action, isn't always the same as position
     *
     * @return action
     */
    @Nonnull
    IAction create(World world, double x, double y, double z, @Nonnull IActionSource source);

    @Nonnull
    @Override
    default IBuilderRegistry<IActionData> getRegistry() {
        return ICBMClassicAPI.ACTION_REGISTRY;
    }
}
