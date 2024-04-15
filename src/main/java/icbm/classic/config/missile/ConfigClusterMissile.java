package icbm.classic.config.missile;

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
}
