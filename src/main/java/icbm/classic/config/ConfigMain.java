package icbm.classic.config;

import icbm.classic.ICBMConstants;
import icbm.classic.content.cluster.missile.ClusterMissileHandler;
import icbm.classic.content.entity.flyingblock.FlyingBlock;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Settings class for various configuration settings.
 *
 * @author Calclavia, DarkCow
 */
@Config(modid = ICBMConstants.DOMAIN, name = "icbmclassic/main")
@Config.LangKey("config.icbmclassic:main.title")
@Mod.EventBusSubscriber(modid = ICBMConstants.DOMAIN)
public class ConfigMain
{
    @Config.Name("use_energy")
    @Config.Comment("Range of tier 1 launcher")
    public static boolean REQUIRES_POWER = true;

    @Config.LangKey("config.icbmclassic:contagious.title")
    @Config.Comment("Settings for contagious poison effect")
    public static ConfigContagious contagiousPoison = new ConfigContagious();

    @SubscribeEvent
    public static void onConfigChangedEvent(final ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(ICBMConstants.DOMAIN))
        {
            ConfigManager.sync(ICBMConstants.DOMAIN, Config.Type.INSTANCE);

            // Reload config so we can convert to easier to hash lists
            FlyingBlock.loadFromConfig();
            ClusterMissileHandler.loadFromConfig();
        }
    }
}
