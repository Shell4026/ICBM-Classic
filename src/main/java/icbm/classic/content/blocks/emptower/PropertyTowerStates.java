package icbm.classic.content.blocks.emptower;

import com.google.common.collect.Lists;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;

public class PropertyTowerStates  extends EnumProperty<PropertyTowerStates.EnumTowerTypes> {
    protected PropertyTowerStates() {
        super("type", EnumTowerTypes.class, Lists.newArrayList(EnumTowerTypes.values()));
    }

    public static enum EnumTowerTypes implements IStringSerializable {
        BASE,
        COIL,
        ELECTRIC,
        SPIN;

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }
}