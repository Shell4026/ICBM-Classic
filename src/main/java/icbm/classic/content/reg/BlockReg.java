package icbm.classic.content.reg;

import icbm.classic.ICBMConstants;
import icbm.classic.api.refs.ICBMExplosives;
import icbm.classic.api.reg.IExplosiveData;
import icbm.classic.content.blocks.*;
import icbm.classic.content.blocks.emptower.BlockEmpTower;
import icbm.classic.content.blocks.emptower.TileEMPTower;
import icbm.classic.content.blocks.emptower.TileEmpTowerFake;
import icbm.classic.content.blocks.explosive.BlockExplosive;
import icbm.classic.content.blocks.launcher.base.BlockLauncherBase;
import icbm.classic.content.blocks.launcher.base.TileLauncherBase;
import icbm.classic.content.blocks.launcher.connector.BlockLaunchConnector;
import icbm.classic.content.blocks.launcher.connector.TileLauncherConnector;
import icbm.classic.content.blocks.launcher.cruise.BlockCruiseLauncher;
import icbm.classic.content.blocks.launcher.cruise.TileCruiseLauncher;
import icbm.classic.content.blocks.launcher.frame.BlockLaunchFrame;
import icbm.classic.content.blocks.launcher.frame.TileLauncherFrame;
import icbm.classic.content.blocks.launcher.screen.BlockLaunchScreen;
import icbm.classic.content.blocks.launcher.screen.TileLauncherScreen;
import icbm.classic.content.blocks.multiblock.TileMulti;
import icbm.classic.content.blocks.radarstation.BlockRadarStation;
import icbm.classic.content.blocks.radarstation.TileRadarStation;
import icbm.classic.content.radioactive.BlockRadioactive;
import net.minecraft.block.Block;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Created by Dark(DarkGuardsman, Robin) on 1/7/19.
 */
@Mod.EventBusSubscriber(modid = ICBMConstants.DOMAIN)
public class BlockReg
{
    @ObjectHolder(ICBMConstants.PREFIX + "glassPressurePlate")
    public static Block blockGlassPlate;

    @ObjectHolder(ICBMConstants.PREFIX + "glassButton")
    public static Block blockGlassButton;

    @ObjectHolder(ICBMConstants.PREFIX + "spikes")
    public static Block blockSpikes;

    @ObjectHolder(ICBMConstants.PREFIX + "concrete")
    public static Block blockConcrete;

    @ObjectHolder(ICBMConstants.PREFIX + "reinforcedGlass")
    public static Block blockReinforcedGlass;

    @ObjectHolder(ICBMConstants.PREFIX + "explosives")
    public static Block blockExplosive;

    @ObjectHolder(ICBMConstants.PREFIX + "launcherbase")
    public static Block blockLaunchBase;

    @ObjectHolder(ICBMConstants.PREFIX + "launcherscreen")
    public static Block blockLaunchScreen;

    @ObjectHolder(ICBMConstants.PREFIX + "launcherframe")
    public static Block blockLaunchSupport;

    @ObjectHolder(ICBMConstants.PREFIX + "launcher_connector")
    public static Block blockLaunchConnector;

    @ObjectHolder(ICBMConstants.PREFIX + "radarStation")
    public static Block blockRadarStation;

    @ObjectHolder(ICBMConstants.PREFIX + "emptower")
    public static Block blockEmpTower;

    @ObjectHolder(ICBMConstants.PREFIX + "cruiseLauncher")
    public static Block blockCruiseLauncher;

    @ObjectHolder(ICBMConstants.PREFIX + "radioactive")
    public static Block blockRadioactive;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(new BlockSpikes().setRegistryName(ICBMConstants.PREFIX + "spikes_normal"));
        event.getRegistry().register(new BlockSpikes().setFire(true).setRegistryName(ICBMConstants.PREFIX + "spikes_fire"));
        event.getRegistry().register(new BlockSpikes().setPoison(true).setRegistryName(ICBMConstants.PREFIX + "spikes_poison"));

        event.getRegistry().register(new Block(
            Block.Properties.create(Material.ROCK)
                .hardnessAndResistance(10, 28)
        ).setRegistryName(ICBMConstants.PREFIX + "concrete_normal"));
        event.getRegistry().register(new Block(
            Block.Properties.create(Material.ROCK)
                .hardnessAndResistance(10, 280)
        ).setRegistryName(ICBMConstants.PREFIX + "concrete_compact"));
        event.getRegistry().register(new Block(
            Block.Properties.create(Material.ROCK)
                .hardnessAndResistance(10, 2800)
        ).setRegistryName(ICBMConstants.PREFIX + "concrete_reinforced"));

        event.getRegistry().register(new GlassBlock(
            Block.Properties.create(Material.GLASS)
                .hardnessAndResistance(10, 40)
        ).setRegistryName(ICBMConstants.PREFIX + "glass_reinforced"));

        event.getRegistry().register(new BlockExplosive(ICBMExplosives.CONDENSED).setRegistryName(ICBMConstants.PREFIX + "explosive_condensed"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.SHRAPNEL).setRegistryName(ICBMConstants.PREFIX + "explosive_shrapnel"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.INCENDIARY).setRegistryName(ICBMConstants.PREFIX + "explosive_incendiary"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.DEBILITATION).setRegistryName(ICBMConstants.PREFIX + "explosive_debilitation"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.CHEMICAL).setRegistryName(ICBMConstants.PREFIX + "explosive_chemical"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.ANVIL).setRegistryName(ICBMConstants.PREFIX + "explosive_anvil"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.REPULSIVE).setRegistryName(ICBMConstants.PREFIX + "explosive_repulsive"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.ATTRACTIVE).setRegistryName(ICBMConstants.PREFIX + "explosive_attractive"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.COLOR).setRegistryName(ICBMConstants.PREFIX + "explosive_color"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.SMOKE).setRegistryName(ICBMConstants.PREFIX + "explosive_smoke"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.FRAGMENTATION).setRegistryName(ICBMConstants.PREFIX + "explosive_fragmentation"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.CONTAGIOUS).setRegistryName(ICBMConstants.PREFIX + "explosive_contagious"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.SONIC).setRegistryName(ICBMConstants.PREFIX + "explosive_sonic"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.BREACHING).setRegistryName(ICBMConstants.PREFIX + "explosive_breaching"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.THERMOBARIC).setRegistryName(ICBMConstants.PREFIX + "explosive_thermobaric"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.NUCLEAR).setRegistryName(ICBMConstants.PREFIX + "explosive_nuclear"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.EMP).setRegistryName(ICBMConstants.PREFIX + "explosive_emp"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.EXOTHERMIC).setRegistryName(ICBMConstants.PREFIX + "explosive_exothermic"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.ENDOTHERMIC).setRegistryName(ICBMConstants.PREFIX + "explosive_endothermic"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.ANTI_GRAVITATIONAL).setRegistryName(ICBMConstants.PREFIX + "explosive_antigravitational"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.ENDER).setRegistryName(ICBMConstants.PREFIX + "explosive_ender"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.ANTIMATTER).setRegistryName(ICBMConstants.PREFIX + "explosive_antimatter"));
        event.getRegistry().register(new BlockExplosive(ICBMExplosives.REDMATTER).setRegistryName(ICBMConstants.PREFIX + "explosive_redmatter"));

        event.getRegistry().register(new BlockEmpTower());
        event.getRegistry().register(new BlockRadarStation());
        event.getRegistry().register(new BlockLaunchFrame());
        event.getRegistry().register(new BlockLaunchConnector());
        event.getRegistry().register(new BlockLauncherBase());
        event.getRegistry().register(new BlockLaunchScreen());

        event.getRegistry().register(new BlockCruiseLauncher());

        event.getRegistry().register(new BlockRadioactive());

        TileEMPTower.register();
        GameRegistry.registerTileEntity(TileEmpTowerFake.class, new ResourceLocation(ICBMConstants.DOMAIN, "emptower_fake"));
        TileRadarStation.register();
        GameRegistry.registerTileEntity(TileLauncherFrame.class, new ResourceLocation(ICBMConstants.DOMAIN, "launcherframe"));
        GameRegistry.registerTileEntity(TileLauncherConnector.class, new ResourceLocation(ICBMConstants.DOMAIN, "launcher_connector"));
        TileLauncherBase.register();
        TileLauncherScreen.register();
        GameRegistry.registerTileEntity(TileMulti.class, new ResourceLocation(ICBMConstants.DOMAIN, "multiblock"));
        TileCruiseLauncher.register();
    }
}
