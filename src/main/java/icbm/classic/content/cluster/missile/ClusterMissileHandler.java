package icbm.classic.content.cluster.missile;

import icbm.classic.config.missile.ConfigMissile;
import icbm.classic.config.util.ItemStackConfigList;
import icbm.classic.content.reg.ItemReg;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ClusterMissileHandler {

    public static final ItemStackConfigList.BooleanOut banAllowItems = new ItemStackConfigList.BooleanOut("[Cluster Contents][Ban/Allow Config]", (configList) -> {
        configList.load(ConfigMissile.CLUSTER_MISSILE.BAN_ALLOW.ITEMS);
    });

    public static final ItemStackConfigList.IntOut itemSizes = new ItemStackConfigList.IntOut("[Cluster Contents][Item Sizes]", (configList) -> {

        configList.set(ItemReg.itemBalloon.getRegistryName(), 2);
        configList.set(ItemReg.itemParachute.getRegistryName(), 2);
        configList.set(ItemReg.itemClusterMissile.getRegistryName(), 20);
        configList.set(ItemReg.itemExplosiveMissile.getRegistryName(), 20);
        configList.set(ItemReg.itemSAM.getRegistryName(), 10);

        configList.load(ConfigMissile.CLUSTER_MISSILE.ITEM_SIZES.ITEMS);
    });

    public static void setup() {
        loadFromConfig();
    }

    public static int sizeOf(ItemStack itemStack) {
        final Integer size = itemSizes.getValue(itemStack);
        return size != null ? size : ConfigMissile.CLUSTER_MISSILE.ITEM_SIZES.DEFAULT_SIZE;
    }

    public static boolean isAllowed(ItemStack itemStack) {
        return ConfigMissile.CLUSTER_MISSILE.BAN_ALLOW.BAN != banAllowItems.getValue(itemStack);
    }

    public static void loadFromConfig() {
        banAllowItems.reload();
        itemSizes.reload();
    }
}
