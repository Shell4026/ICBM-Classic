package icbm.classic.api.actions.cause;

import icbm.classic.api.reg.obj.IBuildableObject;

import javax.annotation.Nullable;

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
    @Nullable
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
    @Nullable
    IActionCause getPreviousCause();

    /**
     * Sets cause before this cause. Current should always be end of chain.
     *
     * @param parent to use
     * @return self
     */
    IActionCause setPreviousCause(@Nullable IActionCause parent);
}
