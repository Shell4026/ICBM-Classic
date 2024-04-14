package icbm.classic.api.actions.data;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Wrapper to provide fields
 *
 */
public interface IActionFieldReceiver {

    /**
     * Called to set a value
     *
     * @param key to set
     * @param value to set
     * @param <T> to allow generic assign, this isn't checked so have fun
     */
    default <T> void setValue(ActionField<T> key, T value) {

    }
}
