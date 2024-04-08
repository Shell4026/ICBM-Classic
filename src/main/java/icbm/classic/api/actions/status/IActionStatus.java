package icbm.classic.api.actions.status;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.reg.obj.IBuildableObject;
import icbm.classic.api.reg.obj.IBuilderRegistry;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

/**
 * Status response for triggering an action. This can be used
 * for various events that were triggered. Both for end users and for
 * developer feedback via the API.
 *
 * Try to avoid referencing the unique keys as catches for exact status types. As these might
 * change to better reflect usage or translation needs. Instead, focus on {@link #isError()} and
 * {@link #isBlocking()} for how to respond. With the message being shown to the user to let
 * them decide how to handle things.
 *
 * If more data is needed additional methods and interfaces may be provided.
 */
public interface IActionStatus extends IBuildableObject {

    @Nonnull
    @Override
    default IBuilderRegistry<IActionStatus> getRegistry() {
        return ICBMClassicAPI.ACTION_STATUS_REGISTRY;
    }

    /**
     * Is the status an error state
     *
     * @return true if error
     */
    boolean isError();

    /**
     * Is the status considered blocking to complete the action. This may be a result of
     * errors, warnings, or other conditions not completed. For example, timer delays would
     * be a non-error style of blocking. While safeties might be considered an error type
     * of blocking.
     *
     * For GUIs this will disable elements.
     *
     * For other cases this may prevent redstone, delay missiles,
     * prevent blasts from trigger, etc.
     *
     * @return true to block triggering of action
     */
    default boolean isBlocking() {
        return isError();
    }

    /**
     * Localization for error output. Assume this
     * will be displayed to a user.
     *
     * @return message
     */
    ITextComponent message();

    // TODO add a status callback for when 'status=aiming` or 'status=delay'
}
