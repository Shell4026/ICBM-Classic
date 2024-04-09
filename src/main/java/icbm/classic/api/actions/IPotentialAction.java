package icbm.classic.api.actions;

import icbm.classic.api.actions.cause.IActionSource;
import icbm.classic.api.actions.status.IActionStatus;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * A potential action to triggered once conditions are met. Included
 * everything that is needed to validate and trigger an action. Short of
 * the location and caused by source.
 */
public interface IPotentialAction {

    /**
     * Action data used to spawn the action into the world
     *
     * @return action data
     */
    @Nonnull
    IActionData getActionData();

    /**
     * Checks the current status of the solution. Validating if requirements are met and nothing
     * is going to fail when {@link #doAction(World, double, double, double, IActionSource)} is called.
     *
     * This is normally called as part of {@link #doAction(World, double, double, double, IActionSource)} and
     * only exists as a way to show predicted result. Useful for user feedback related to potential actions.
     *
     * @param world to create action
     * @param x location
     * @param y location
     * @param z location
     * @param source triggering the action
     * @return response
     */
    @Nonnull
    IActionStatus checkAction(World world, double x, double y, double z, @Nonnull IActionSource source) ;

    /**
     * Wraps to {@link IActionData#create(World, double, double, double, IActionSource)} after validating
     * solution state and if conditions are met to trigger.
     *
     * @param world to create action
     * @param x location
     * @param y location
     * @param z location
     * @param source triggering the action
     * @return response
     */
    @Nonnull
    IActionStatus doAction(World world, double x, double y, double z, @Nonnull IActionSource source);
}
