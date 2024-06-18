package icbm.classic.content.reg;

import icbm.classic.ICBMConstants;
import icbm.classic.content.blocks.emptower.TileEMPTower;
import icbm.classic.content.blocks.emptower.TileEmpTowerFake;
import icbm.classic.content.blocks.launcher.base.TileLauncherBase;
import icbm.classic.content.blocks.launcher.connector.TileLauncherConnector;
import icbm.classic.content.blocks.launcher.cruise.TileCruiseLauncher;
import icbm.classic.content.blocks.launcher.frame.TileLauncherFrame;
import icbm.classic.content.blocks.launcher.screen.TileLauncherScreen;
import icbm.classic.content.blocks.multiblock.TileMulti;
import icbm.classic.content.blocks.radarstation.TileRadarStation;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Created by Dark(DarkGuardsman, Robin) on 1/7/19.
 */
@Mod.EventBusSubscriber(modid = ICBMConstants.DOMAIN)
public class TileReg
{
    public static final DeferredRegister<TileEntityType<?>> TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, ICBMConstants.DOMAIN);

    public static final TileEntityType<?> EMP_TOWER_BASE_TILE = TileEntityType.Builder.create(TileEMPTower::new, BlockReg.EM)
        .build(null).setRegistryName(new ResourceLocation(ICBMConstants.DOMAIN, "emp_tower_base"));


    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> evt) {
        TileEMPTower.register();
        evt.getRegistry().register(EMP_TOWER_BASE_TILE);
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
