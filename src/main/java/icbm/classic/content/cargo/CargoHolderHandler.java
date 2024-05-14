package icbm.classic.content.cargo;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.reg.IExplosiveData;
import icbm.classic.config.missile.ConfigMissile;
import icbm.classic.config.util.ItemStackConfigList;
import icbm.classic.content.reg.ItemReg;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.ItemStack;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class CargoHolderHandler {
    public static final ItemStackConfigList.BooleanOut banAllowItems = new ItemStackConfigList.BooleanOut("[CargoHolder][Ban/Allow Config]", (configList) -> {
        configList.load(ConfigMissile.CARGO_HOLDERS.BAN_ALLOW.ITEMS);
    });

    public static void setup() {
        loadFromConfig();
    }

    public static boolean isAllowed(ItemStack itemStack) {
        return ConfigMissile.CARGO_HOLDERS.BAN_ALLOW.BAN == banAllowItems.isAllowed(itemStack);
    }

    public static void loadFromConfig() {
        banAllowItems.reload();
    }
}
