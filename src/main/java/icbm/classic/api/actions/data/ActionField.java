package icbm.classic.api.actions.data;

import lombok.Value;

import java.lang.reflect.Type;

/**
 * Metadata about dynamic fields
 */
@Value
public class ActionField<T> {
    String key;
    Type type;

    public T cast(Object v) {
        return (T) v;
    }
}
