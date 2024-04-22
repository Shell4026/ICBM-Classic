package icbm.classic.lib.actions.fields;

import icbm.classic.api.actions.data.ActionField;
import icbm.classic.api.actions.data.IActionFieldProvider;
import net.minecraft.nbt.NBTBase;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ActionFieldProvider implements IActionFieldProvider {
    private final Map<ActionField, Supplier> fieldAccessors = new HashMap<>();

    public <VALUE, TAG extends NBTBase> ActionFieldProvider field(ActionField<VALUE, TAG> field, Supplier<VALUE> accessor) {
        fieldAccessors.put(field, accessor);
        return this;
    }

    @Override
    public <VALUE, TAG extends NBTBase> VALUE getValue(ActionField<VALUE, TAG> key) {
        Supplier<VALUE> supplier = fieldAccessors.get(key);
        if(supplier == null) {
            return null;
        }
        return supplier.get();
    }

    @Override
    public <VALUE, TAG extends NBTBase> boolean hasField(ActionField<VALUE, TAG> key) {
        return fieldAccessors.containsKey(key);
    }
}
