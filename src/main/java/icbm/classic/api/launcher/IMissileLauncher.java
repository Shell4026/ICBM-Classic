package icbm.classic.api.launcher;

import icbm.classic.api.actions.cause.IActionCause;
import icbm.classic.api.actions.cause.IActionSource;
import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.api.missiles.parts.IMissileTarget;
import icbm.classic.config.missile.ConfigMissile;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Capability for accessing data about a launcher
 *
 * Created by Dark(DarkGuardsman, Robin) on 1/9/19.
 */
public interface IMissileLauncher
{

    /**
     * Unique ID to reference this launcher. Used for a mix of logging, events,
     * and mod interactions. This should be persisted and should never change.
     *
     * @return unique id
     */
    UUID getUniqueId();

    /**
     * Direct way to get launcher's current status
     *
     * {@link #launch(IMissileTarget, IActionCause, boolean)} will often invoke this in addition
     * to other logic. This only allows exacting status without triggering pre-checks or launch
     * results. Useful for checking how the launcher itself is doing and not what launcher will do
     * with the missile.
     *
     * @return current status
     */
    IActionStatus getStatus();

    /**
     * Direct way to get launcher's validate result of the target and cause.
     *
     * {@link #launch(IMissileTarget, IActionCause, boolean)} will often invoke this in addition
     * to other logic. This only allows exacting pre-validation logic directly without worrying
     * about launch results.
     *
     * @return status from pre-checks
     *
     * @deprecated will be replaced with {@link icbm.classic.api.actions.IPotentialAction#checkAction(World, double, double, double, IActionSource)}
     */
    IActionStatus preCheckLaunch(IMissileTarget target, @Nullable IActionCause cause);

    /**
     * Tries to launch the missile
     *
     * Ensure that simulation logic doesn't trigger any lasting effects on the launcher. As
     * different systems will use simulate as a way to predict launcher behavior. Including
     * seeing what status the launcher will output. As well any issues with firing the missile.
     *
     * @param firingSolution to drive how missiles are fired and what target they use
     * @param cause to note, optional but recommended to create a history of firing reason
     * @param simulate to do pre-flight checks and get current status
     * @return status of launch
     *
     * @deprecated will be replaced with {@link icbm.classic.api.actions.IPotentialAction#doAction(World, double, double, double, IActionSource)}
     */
    IActionStatus launch(ILauncherSolution firingSolution, @Nullable IActionCause cause, boolean simulate);


    /**
     * @Deprecated use {@link #launch(ILauncherSolution, IActionCause, boolean)}
     */
    @Deprecated
    default IActionStatus launch(IMissileTarget target, @Nullable IActionCause cause, boolean simulate) {
        return launch((launcher) -> target, cause, simulate);
    }

    /**
     * Index of the launcher in the network. Used
     * in combination with group to provide indexing
     * for chain fire
     *
     * @return index or -1 to ignore grouping
     */
    default int getLaunchIndex() {
        // TODO future state
        return -1;
    }

    /**
     * Group of the launcher, used for chain firing
     * and limiting which launchers go to which
     * controller.
     *
     * @return index or -1 to ignore grouping
     */
    default int getLauncherGroup() {
        // TODO future state
        return -1;
    }

    /**
     * Gets the predicted inaccuracy for the fire mission
     *
     * @param predictedTarget that might be passed to the launcher
     * @param launchers count being used in the fire mission
     * @return inaccuracy
     */
    default float getInaccuracy(Vec3d predictedTarget, int launchers) {
        return 0;
    }

    /**
     * Velocity of the current ready to go payload. Used for target prediction
     * and intercept calculations.
     *
     * @return velocity in meters per tick
     */
    default float getPayloadVelocity() {
        return ConfigMissile.DIRECT_FLIGHT_SPEED;
    }
}
