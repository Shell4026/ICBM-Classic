package icbm.classic.content.actions.fields;

import icbm.classic.api.actions.data.ActionField;
import icbm.classic.api.actions.data.IActionFieldProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ActionFieldProvider implements IActionFieldProvider {
    private final Map<ActionField, Supplier> fieldAccessors = new HashMap<>();

    public <T> ActionFieldProvider field(ActionField<T> field, Supplier<T> accessor) {
        fieldAccessors.put(field, accessor);
        return this;
    }

    @Override
    public <T> T getValue(ActionField<T> key) {
        return (T) fieldAccessors.get(key);
    }

    @Override
    public <T> boolean hasField(ActionField<T> key) {
        return fieldAccessors.containsKey(key);
    }
}
