package icbm.classic.content.cluster.missile;

import icbm.classic.config.missile.ConfigMissile;
import icbm.classic.config.util.ItemStackConfigList;
import icbm.classic.content.reg.ItemReg;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ClusterMissileHandler {

    public static final ItemStackConfigList banAllowItems = new ItemStackConfigList("[Cluster Contents][Ban/Allow Config]", (configList) -> {
        configList.load(ConfigMissile.CLUSTER_MISSILE.BAN_ALLOW.ITEMS);
    });

    public static final int MAX_SIZE = 200;
    public static HashMap<Item, Integer> SIZES = new HashMap<>();

    public static void setup() {
        SIZES.put(ItemReg.itemBalloon, 2);
        SIZES.put(ItemReg.itemParachute, 2);
        SIZES.put(ItemReg.itemClusterMissile, 20);
        SIZES.put(ItemReg.itemExplosiveMissile, 20);
        SIZES.put(ItemReg.itemSAM, 10);

        loadFromConfig();
    }

    public static int sizeOf(ItemStack itemStack) {
        if(SIZES.containsKey(itemStack.getItem())) {
            return SIZES.get(itemStack.getItem());
        }
        return 1;
    }

    public static boolean isAllowed(ItemStack itemStack) {
        return ConfigMissile.CLUSTER_MISSILE.BAN_ALLOW.BAN != banAllowItems.contains(itemStack);
    }

    public static void loadFromConfig() {
        banAllowItems.reload();
    }
}
