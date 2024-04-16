package icbm.classic.config.missile;

import icbm.classic.config.ConfigFlyingBlocks;
import net.minecraftforge.common.config.Config;

/**
 * Config for Surface to Air missiles
 */
public class ConfigClusterMissile
{
    @Config.Name("health")
    @Config.Comment("Amount of damage a missile can take from any source before death")
    @Config.RangeDouble(min = 0.0001, max = 10)
    public float MAX_HEALTH = 100;

    @Config.Name("item_ban_allow")
    @Config.Comment("Items allowed for insertion into cluster missile as a projectile payload")
    public BanList BAN_ALLOW = new BanList();

    public static class BanList {

        @Config.Name("ban")
        @Config.Comment("Set to true to ban all blocks contained. False to use as allow list")
        public boolean BAN = true;

        @Config.Name("list")
        @Config.Comment("Item/ItemStack names. Docs: https://github.com/BuiltBrokenModding/ICBM-Classic/wiki/config-itemstack")
        public String[] ITEMS = new String[]{"minecraft:fire"};
    }

}
