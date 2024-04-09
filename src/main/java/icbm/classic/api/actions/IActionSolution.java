package icbm.classic.api.actions;

import icbm.classic.api.actions.cause.IActionSource;
import icbm.classic.api.actions.conditions.ICondition;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A solution of everything need to pre-check, and create an action
 */
public interface IActionSolution {

    /**
     * Action data used to spawn the action into the world
     *
     * @return action data
     */
    @Nonnull
    IActionData getActionData();

    @Nonnull
    default IAction create(World world, double x, double y, double z, @Nonnull IActionSource source) {
        return this.getActionData().create(world, x, y, z, source);
    }

    /**
     * Condition(s) required to be completed in order to create the action
     *
     * @return conditions or null if none are required
     */
    @Nullable
    ICondition getStartCondition();

    // TODO methods to add customizations
}
