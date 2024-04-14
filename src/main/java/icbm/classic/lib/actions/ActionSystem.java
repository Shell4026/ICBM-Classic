package icbm.classic.lib.actions;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.actions.cause.IActionCause;
import icbm.classic.api.actions.conditions.ICondition;
import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.api.reg.events.ActionRegistryEvent;
import icbm.classic.api.reg.events.ActionStatusRegistryEvent;
import icbm.classic.api.reg.events.ConditionalRegistryEvent;
import icbm.classic.api.reg.events.MissileCauseRegistryEvent;
import icbm.classic.content.actions.emp.ActionDataEmpArea;
import icbm.classic.content.blocks.launcher.screen.BlockScreenCause;
import icbm.classic.content.blocks.launcher.status.LauncherStatus;
import icbm.classic.content.missile.logic.source.cause.CausedByBlock;
import icbm.classic.content.missile.logic.source.cause.EntityCause;
import icbm.classic.content.missile.logic.source.cause.RedstoneCause;
import icbm.classic.lib.actions.conditionals.timer.TimerCondition;
import icbm.classic.lib.actions.conditionals.timer.TimerTickingStatus;
import icbm.classic.lib.actions.listners.ActionListenerHandler;
import icbm.classic.lib.actions.status.ActionResponses;
import icbm.classic.lib.buildable.BuildableObjectRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.common.MinecraftForge;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ActionSystem {
    public static void setup()
    {
        ICBMClassicAPI.ACTION_LISTENER = new ActionListenerHandler();
        setupConditionalRegistry();
        setupCauseRegistry();
        setupStatusRegistry();
        setupActionRegistry();
    }

    private static void setupConditionalRegistry() {
        ICBMClassicAPI.CONDITION_REGISTRY =  new BuildableObjectRegistry<ICondition>("CONDITIONALS", "conditionals");

        ICBMClassicAPI.CONDITION_REGISTRY.register(TimerCondition.REG_NAME, TimerCondition::new);

        //Fire registry event
        MinecraftForge.EVENT_BUS.post(new ConditionalRegistryEvent(ICBMClassicAPI.CONDITION_REGISTRY));

        //Lock to prevent late registry
        ((BuildableObjectRegistry)ICBMClassicAPI.CONDITION_REGISTRY).lock();
    }

    private static void setupStatusRegistry() {
        ICBMClassicAPI.ACTION_STATUS_REGISTRY =  new BuildableObjectRegistry<IActionStatus>("ACTION_STATUS", "action.status");

        // Register defaults
        LauncherStatus.registerTypes();
        ActionResponses.registerTypes();
        ICBMClassicAPI.ACTION_STATUS_REGISTRY.register(TimerTickingStatus.REG_NAME, TimerTickingStatus::new);

        //Fire registry event
        MinecraftForge.EVENT_BUS.post(new ActionStatusRegistryEvent(ICBMClassicAPI.ACTION_STATUS_REGISTRY));

        //Lock to prevent late registry
        ((BuildableObjectRegistry)ICBMClassicAPI.ACTION_STATUS_REGISTRY).lock();
    }

    private static void setupActionRegistry() {
        ICBMClassicAPI.ACTION_REGISTRY =  new BuildableObjectRegistry<>("ACTION_DATA", "action.data");

        // Register defaults
        new ActionDataEmpArea().register();

        //Fire registry event
        MinecraftForge.EVENT_BUS.post(new ActionRegistryEvent(ICBMClassicAPI.ACTION_REGISTRY));

        //Lock to prevent late registry
        ((BuildableObjectRegistry)ICBMClassicAPI.ACTION_REGISTRY).lock();
    }

    private static void setupCauseRegistry()
    {
        ICBMClassicAPI.ACTION_CAUSE_REGISTRY =  new BuildableObjectRegistry<IActionCause>("ACTION_CAUSE", "action.cause");;

        // Register defaults
        ICBMClassicAPI.ACTION_CAUSE_REGISTRY.register(EntityCause.REG_NAME, EntityCause::new);
        ICBMClassicAPI.ACTION_CAUSE_REGISTRY.register(CausedByBlock.REG_NAME, CausedByBlock::new);
        ICBMClassicAPI.ACTION_CAUSE_REGISTRY.register(BlockScreenCause.REG_NAME, BlockScreenCause::new);
        ICBMClassicAPI.ACTION_CAUSE_REGISTRY.register(RedstoneCause.REG_NAME, RedstoneCause::new);

        //Fire registry event
        MinecraftForge.EVENT_BUS.post(new MissileCauseRegistryEvent(ICBMClassicAPI.ACTION_CAUSE_REGISTRY));

        //Lock to prevent late registry
        ((BuildableObjectRegistry)ICBMClassicAPI.ACTION_CAUSE_REGISTRY).lock();
    }
}
