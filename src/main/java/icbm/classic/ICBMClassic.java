package icbm.classic;

import com.mojang.brigadier.CommandDispatcher;
import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.missiles.parts.IMissileFlightLogic;
import icbm.classic.api.missiles.parts.IMissileTarget;
import icbm.classic.api.reg.IExplosiveCustomization;
import icbm.classic.api.reg.events.*;
import icbm.classic.client.ICBMCreativeTab;
import icbm.classic.command.ICBMCommands;
import icbm.classic.command.system.CommandEntryPoint;
import icbm.classic.config.ConfigThread;
import icbm.classic.content.cargo.CargoHolderHandler;
import icbm.classic.content.cluster.missile.ClusterMissileHandler;
import icbm.classic.content.missile.logic.flight.move.MoveByVec3Logic;
import icbm.classic.content.radioactive.RadioactiveHandler;
import icbm.classic.lib.actions.ActionSystem;
import icbm.classic.content.blast.caps.CapabilityBlast;
import icbm.classic.content.blast.caps.CapabilityBlastVelocity;
import icbm.classic.content.cluster.bomblet.BombletProjectileData;
import icbm.classic.content.blast.ender.EnderBlastCustomization;
import icbm.classic.content.cargo.balloon.BalloonProjectileData;
import icbm.classic.content.entity.flyingblock.FlyingBlock;
import icbm.classic.content.items.behavior.BombCartDispenseBehavior;
import icbm.classic.content.items.behavior.GrenadeDispenseBehavior;
import icbm.classic.lib.buildable.BuildableObjectRegistry;
import icbm.classic.content.missile.entity.CapabilityMissile;
import icbm.classic.content.missile.entity.anti.SAMTargetData;
import icbm.classic.content.missile.logic.flight.*;
import icbm.classic.content.missile.logic.flight.move.MoveByFacingLogic;
import icbm.classic.content.missile.logic.flight.move.MoveForTicksLogic;
import icbm.classic.content.missile.logic.targeting.BallisticTargetingData;
import icbm.classic.content.missile.logic.targeting.BasicTargetData;
import icbm.classic.content.cargo.parachute.ParachuteProjectileData;
import icbm.classic.content.potion.ContagiousPoison;
import icbm.classic.content.reg.ExplosiveInit;
import icbm.classic.content.reg.ItemReg;
import icbm.classic.lib.capability.chicken.CapSpaceChicken;
import icbm.classic.lib.capability.emp.CapabilityEMP;
import icbm.classic.lib.capability.ex.CapabilityExplosive;
import icbm.classic.lib.capability.gps.CapabilityGPSData;
import icbm.classic.lib.capability.launcher.CapabilityMissileHolder;
import icbm.classic.lib.capability.launcher.CapabilityMissileLauncher;
import icbm.classic.lib.capability.missile.CapabilityMissileStack;
import icbm.classic.lib.projectile.CapabilityProjectileStack;
import icbm.classic.lib.energy.system.EnergySystem;
import icbm.classic.lib.energy.system.EnergySystemFE;
import icbm.classic.lib.explosive.reg.*;
import icbm.classic.lib.network.netty.PacketManager;
import icbm.classic.lib.projectile.ProjectileDataRegistry;
import icbm.classic.lib.radar.RadarRegistry;
import icbm.classic.lib.radio.CapabilityRadio;
import icbm.classic.lib.radio.RadioRegistry;
import icbm.classic.lib.thread.WorkerThreadManager;
import icbm.classic.lib.tracker.EventTracker;
import icbm.classic.lib.world.ProjectileBlockInteraction;
import net.minecraft.block.DispenserBlock;
import net.minecraft.command.CommandSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Mod class for ICBM Classic, contains all loading code and references to objects crated by the mod.
 *
 * @author Dark(DarkGuardsman, Robin).
 * <p>
 * Orginal author and creator of the mod: Calclavia
 */
@Mod(ICBMConstants.DOMAIN)
@Mod.EventBusSubscriber
public class ICBMClassic
{
    public static final int DATA_FIXER_VERSION = 5;

    public static final boolean runningAsDev = System.getProperty("development") != null && System.getProperty("development").equalsIgnoreCase("true");

    public static ICBMClassic INSTANCE;

    //@SidedProxy(clientSide = "icbm.classic.client.ClientProxy", serverSide = "icbm.classic.CommonProxy")
    public static CommonProxy proxy;

    public static final int MAP_HEIGHT = 255;

    @Deprecated
    private static final Logger logger = LogManager.getLogger(ICBMConstants.DOMAIN);

    public static final PacketManager packetHandler = new PacketManager(ICBMConstants.DOMAIN);

    public static final ContagiousPoison chemicalPotion = new ContagiousPoison("Chemical", 0, false);
    public static final ContagiousPoison contagiousPotion = new ContagiousPoison("Contagious", 1, true);

    public static final ICBMCreativeTab CREATIVE_TAB = new ICBMCreativeTab(ICBMConstants.DOMAIN);

    public static final EventTracker MAIN_TRACKER = new EventTracker();

    public ICBMClassic() {
        INSTANCE = this;
    }

    /*@SubscribeEvent TODO likely moved to JSON
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        if (ConfigItems.ENABLE_CRAFTING_ITEMS)
        {
            if (ConfigItems.ENABLE_INGOTS_ITEMS)
            {
                //Steel clump -> Steel ingot
                GameRegistry.addSmelting(new ItemStack(ItemReg.itemIngotClump, 1, 0), new ItemStack(ItemReg.itemIngot, 1, 0), 0.1f);
            }

            if (ConfigItems.ENABLE_PLATES_ITEMS)
            {
                //Fix for removing recipe of plate
                GameRegistry.addSmelting(ItemReg.itemPlate.getStack("iron", 1), new ItemStack(Items.IRON_INGOT), 0f);
            }
        }

        GameRegistry.addSmelting(new ItemStack(ItemReg.itemSaltpeterBall, 1, 0), new ItemStack(ItemReg.itemSaltpeterDust, 1, 0), 0.1f);

        // Dynamic item recipes
        event.getRegistry().register(new RecipeCargoData(new ItemStack(ItemReg.itemBalloon), BalloonProjectileData::new).setRegistryName(new ResourceLocation(ICBMConstants.DOMAIN, "balloon_cargo")));
        event.getRegistry().register(new RecipeCargoData(new ItemStack(ItemReg.itemParachute), ParachuteProjectileData::new).setRegistryName(new ResourceLocation(ICBMConstants.DOMAIN, "parachute_cargo")));
        event.getRegistry().register(new RecipeCluster(new ItemStack(ItemReg.itemClusterMissile)).setRegistryName(new ResourceLocation(ICBMConstants.DOMAIN, "cluster_missile")));
    }*/

   /* @SubscribeEvent TODO this may be done by JSON now?
    public static void registerLoot(LootTableLoadEvent event)
    {
        final String VANILLA_LOOT_POOL_ID = "main";
        if (event.getName().equals(EntityType.CREEPER.getLootTable()) || event.getName().equals(EntityType.BLAZE.getLootTable()))
        {
            if (ConfigItems.ENABLE_SULFUR_LOOT_DROPS)
            {
                LootPool lootPool = event.getTable().getPool(VANILLA_LOOT_POOL_ID);
                if (lootPool != null)
                {
                    lootPool.add(new LootEntryItemStack(ICBMConstants.PREFIX + "sulfur", new ItemStack(ItemReg.itemSulfurDust, 10, 0), 2, 0));
                }
            }
        }
    }*/


    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event)
    {
        proxy.preInit();
        EnergySystem.register(new EnergySystemFE());

        //Register caps
        registerCapabilities();

        MinecraftForge.EVENT_BUS.register(RadarRegistry.INSTANCE);
        MinecraftForge.EVENT_BUS.register(RadioRegistry.INSTANCE);
        //NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

        handleMissileTargetRegistry();
        handleMissileFlightRegistry();
        ActionSystem.setup();
        handleExplosiveCustomizationRegistry();
        handleExRegistry();
        handleProjectileDataRegistry();

        proxy.init();
        packetHandler.init();
        CREATIVE_TAB.init();
        ProjectileBlockInteraction.register();
        ClusterMissileHandler.setup();
        CargoHolderHandler.setup();
        RadioactiveHandler.setup();

        // Needs to lock late as we need content to register some types
        ((ProjectileDataRegistry) ICBMClassicAPI.PROJECTILE_DATA_REGISTRY).registerVanillaDefaults();
        MinecraftForge.EVENT_BUS.post(new ProjectileDataRegistryEvent(ICBMClassicAPI.PROJECTILE_DATA_REGISTRY));
        ((ProjectileDataRegistry) ICBMClassicAPI.PROJECTILE_DATA_REGISTRY).lock();

        /** Dispenser Handler */
        if (ItemReg.itemGrenade != null)
        {
            DispenserBlock.registerDispenseBehavior(ItemReg.itemGrenade, new GrenadeDispenseBehavior());
        }

        if (ItemReg.itemBombCart != null)
        {
            DispenserBlock.registerDispenseBehavior(ItemReg.itemBombCart, new BombCartDispenseBehavior());
        }

        // Generate defaults
        FlyingBlock.loadFromConfig();
    }

    void registerCapabilities() {
        CapabilityEMP.register();
        CapabilityMissile.register();
        CapabilityExplosive.register();
        CapabilityBlast.register();
        CapabilityBlastVelocity.register();
        CapabilityMissileHolder.register();
        CapabilityMissileStack.register();
        CapabilityProjectileStack.register();
        CapabilityMissileLauncher.register();
        CapabilityRadio.register();
        CapSpaceChicken.register();
        CapabilityGPSData.register();
    }

    void handleMissileTargetRegistry()
    {
        ICBMClassicAPI.MISSILE_TARGET_DATA_REGISTRY = new BuildableObjectRegistry<IMissileTarget>("TARGET_DATA");

        // Default types
        ICBMClassicAPI.MISSILE_TARGET_DATA_REGISTRY.register(BasicTargetData.REG_NAME, BasicTargetData::new);
        ICBMClassicAPI.MISSILE_TARGET_DATA_REGISTRY.register(BallisticTargetingData.REG_NAME, BallisticTargetingData::new);
        ICBMClassicAPI.MISSILE_TARGET_DATA_REGISTRY.register(SAMTargetData.REG_NAME, () -> null); //Can't be restored from save but reserving name

        //Fire registry event
        MinecraftForge.EVENT_BUS.post(new MissileTargetRegistryEvent(ICBMClassicAPI.MISSILE_TARGET_DATA_REGISTRY));

        //Lock to prevent late registry
        ((BuildableObjectRegistry)ICBMClassicAPI.MISSILE_TARGET_DATA_REGISTRY).lock();
    }

    void handleMissileFlightRegistry()
    {
        ICBMClassicAPI.MISSILE_FLIGHT_LOGIC_REGISTRY = new BuildableObjectRegistry<IMissileFlightLogic>("FLIGHT_LOGIC");

        // Register defaults
        ICBMClassicAPI.MISSILE_FLIGHT_LOGIC_REGISTRY.register(DirectFlightLogic.REG_NAME, DirectFlightLogic::new);
        ICBMClassicAPI.MISSILE_FLIGHT_LOGIC_REGISTRY.register(BallisticFlightLogicOld.REG_NAME, BallisticFlightLogicOld::new);
        ICBMClassicAPI.MISSILE_FLIGHT_LOGIC_REGISTRY.register(ArcFlightLogic.REG_NAME, ArcFlightLogic::new);
        ICBMClassicAPI.MISSILE_FLIGHT_LOGIC_REGISTRY.register(WarmupFlightLogic.REG_NAME, WarmupFlightLogic::new);
        ICBMClassicAPI.MISSILE_FLIGHT_LOGIC_REGISTRY.register(MoveByFacingLogic.REG_NAME, MoveByFacingLogic::new);
        ICBMClassicAPI.MISSILE_FLIGHT_LOGIC_REGISTRY.register(MoveByVec3Logic.REG_NAME, MoveByVec3Logic::new);
        ICBMClassicAPI.MISSILE_FLIGHT_LOGIC_REGISTRY.register(MoveForTicksLogic.REG_NAME, MoveForTicksLogic::new);
        ICBMClassicAPI.MISSILE_FLIGHT_LOGIC_REGISTRY.register(DeadFlightLogic.REG_NAME, DeadFlightLogic::new);
        ICBMClassicAPI.MISSILE_FLIGHT_LOGIC_REGISTRY.register(FollowTargetLogic.REG_NAME, FollowTargetLogic::new);


        //Fire registry event
        MinecraftForge.EVENT_BUS.post(new MissileFlightLogicRegistryEvent(ICBMClassicAPI.MISSILE_FLIGHT_LOGIC_REGISTRY));

        //Lock to prevent late registry
        ((BuildableObjectRegistry)ICBMClassicAPI.MISSILE_FLIGHT_LOGIC_REGISTRY).lock();
    }

    void handleExplosiveCustomizationRegistry()
    {
        ICBMClassicAPI.EXPLOSIVE_CUSTOMIZATION_REGISTRY =  new BuildableObjectRegistry<IExplosiveCustomization>("EXPLOSIVE_CUSTOMIZATION");

        // Register defaults
        ICBMClassicAPI.EXPLOSIVE_CUSTOMIZATION_REGISTRY.register(EnderBlastCustomization.NAME, EnderBlastCustomization::new);

        //Fire registry event
        MinecraftForge.EVENT_BUS.post(new ExplosiveCustomizationRegistryEvent(ICBMClassicAPI.EXPLOSIVE_CUSTOMIZATION_REGISTRY));

        //Lock to prevent late registry
        ((BuildableObjectRegistry) ICBMClassicAPI.EXPLOSIVE_CUSTOMIZATION_REGISTRY).lock();
    }

    void handleProjectileDataRegistry()
    {
        ICBMClassicAPI.PROJECTILE_DATA_REGISTRY =  new ProjectileDataRegistry();

        // Register defaults
        ICBMClassicAPI.PROJECTILE_DATA_REGISTRY.register(BombletProjectileData.NAME, BombletProjectileData::new);
        ICBMClassicAPI.PROJECTILE_DATA_REGISTRY.register(ParachuteProjectileData.NAME, ParachuteProjectileData::new);
        ICBMClassicAPI.PROJECTILE_DATA_REGISTRY.register(BalloonProjectileData.NAME, BalloonProjectileData::new);
    }

    void handleExRegistry()
    {
        //Init registry
        final ExplosiveRegistry explosiveRegistry = new ExplosiveRegistry();
        ICBMClassicAPI.EXPLOSIVE_REGISTRY = explosiveRegistry;

        ICBMClassicAPI.EX_BLOCK_REGISTRY = new ExBlockContentReg();
        ICBMClassicAPI.EX_GRENADE_REGISTRY = new ExGrenadeContentReg();
        ICBMClassicAPI.EX_MINECART_REGISTRY = new ExMinecartContentReg();
        ICBMClassicAPI.EX_MISSILE_REGISTRY = new ExMissileContentReg();

        //Register default content types
        explosiveRegistry.registerContentRegistry(ICBMClassicAPI.EX_BLOCK_REGISTRY);
        explosiveRegistry.registerContentRegistry(ICBMClassicAPI.EX_GRENADE_REGISTRY);
        explosiveRegistry.registerContentRegistry(ICBMClassicAPI.EX_MISSILE_REGISTRY);
        explosiveRegistry.registerContentRegistry(ICBMClassicAPI.EX_MINECART_REGISTRY);

        //Fire registry events for content types
        MinecraftForge.EVENT_BUS.post(new ExplosiveContentRegistryEvent(explosiveRegistry));

        //Lock content types, done to prevent errors with adding content
        explosiveRegistry.lockNewContentTypes();

        //Register internal first to reserve slots for backwards compatibility
        ExplosiveInit.init();

        //Fire registry event for explosives
        MinecraftForge.EVENT_BUS.post(new ExplosiveRegistryEvent(explosiveRegistry));
        explosiveRegistry.lockNewExplosives();

        //Lock all registry, done to prevent errors in data generation for renders and content
        explosiveRegistry.completeLock();
    }


    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent event)
    {
        //Get command manager
        CommandDispatcher<CommandSource> commandManager = event.getCommandDispatcher();

        //Setup commands
        ICBMCommands.init();

        //Register main command
        commandManager.register(new CommandEntryPoint("icbm", ICBMCommands.ICBM_COMMAND));

        WorkerThreadManager.INSTANCE = new WorkerThreadManager(ConfigThread.THREAD_COUNT);
        WorkerThreadManager.INSTANCE.startThreads();
    }

    @SubscribeEvent
    public void serverStopping(FMLServerStoppingEvent event)
    {
        WorkerThreadManager.INSTANCE.killThreads();
    }

    public static Logger logger()
    {
        return logger;
    }
}
