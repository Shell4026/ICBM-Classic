package icbm.classic.config;

import icbm.classic.ICBMConstants;
import net.minecraftforge.common.config.Config;

public class ConfigContagious {
    @Config.Name("damage")
    @Config.Comment("Damage per cycle to do to players impacted by contagious damage")
    @Config.RangeDouble(min = 0)
    public float damage = 1f;

    @Config.Name("cycle")
    @Config.Comment("Time (ticks) to wait between cycling poison effects")
    @Config.RangeInt(min = 1)
    public int cycle = ICBMConstants.TICKS_SEC * 2;

    @Config.Name("range")
    @Config.Comment("Distance (meters) for virus to spread")
    @Config.RangeInt(min = 1)
    public int range = 13;

    @Config.Name("chance")
    @Config.Comment("Chance for the virus to spread to nearby entities. Lower values trigger spread more often (r > c).")
    @Config.RangeDouble(min = 0)
    public float chance = 0.8f;
}
