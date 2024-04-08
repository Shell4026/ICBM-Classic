package icbm.classic.api.actions.cause;

import icbm.classic.api.reg.obj.IBuildableObject;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Cause of an action. Stored as part of {@link IActionSource}
 * used to track who or what triggered an action. Can be stored as a chain of
 * causes allowing detailed information to be tracked.
 *
 * Example: player -> remote -> screen -> silo -> cluster missile -> missile
 */
public interface IActionCause extends IBuildableObject {

    /**
     * First cause in the history
     *
     * @return first entry
     */
    default IActionCause getRootCause() {
        final IActionCause cause = getPreviousCause();
        if(cause != null) { //TODO add logic to prevent infinite loop
            return cause.getPreviousCause();
        }
        return null;
    }

    /**
     * Parent cause in the history
     *
     * @return cause
     */
    IActionCause getPreviousCause();

    /**
     * Sets the missile cause
     * @param parent to use
     * @return self
     */
    IActionCause setPreviousCause(IActionCause parent);

    /**
     * Cause containing entity information
     */
    interface IEntityCause extends IActionCause {
        Entity getEntity();
    }

    /**
     * Cause containing block information
     */
    interface IBlockCause extends IActionCause {
        World getWorld();
        BlockPos getBlockPos();
        IBlockState getBlockState();
    }
}
