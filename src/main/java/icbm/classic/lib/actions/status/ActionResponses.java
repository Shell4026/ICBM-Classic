package icbm.classic.lib.actions.status;

import icbm.classic.ICBMConstants;
import icbm.classic.api.actions.status.IActionStatus;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class ActionResponses {
    // <editor-fold description="error status">
    /** Unexpected errors that don't align with any specific type... usually lazy dev using try-catch */
    public static IActionStatus UNKNOWN_ERROR = ImmutableStatus.error(new ResourceLocation(ICBMConstants.DOMAIN, "action.error.unknown"));

    /** Spawning via {@link World#spawnEntity(Entity)} failed. Could be due to chunk unloaded, join world event, or other unknown reason */
    public static IActionStatus ENTITY_SPAWN_FAILED = ImmutableStatus.error(new ResourceLocation(ICBMConstants.DOMAIN, "action.entity.spawning.failed"));

    /** Canceled due to {@link net.minecraftforge.event.ForgeEventFactory#onExplosionStart(World, Explosion)} */
    public static IActionStatus EXPLOSION_CANCELED = ImmutableStatus.blocking(new ResourceLocation(ICBMConstants.DOMAIN, "action.explosion.canceled"));

    /** Generic validation issue with action state */
    public static IActionStatus VALIDATION_ERROR = ImmutableStatus.blocking(new ResourceLocation(ICBMConstants.DOMAIN, "action.validation.failed"));

    public static IActionStatus BLOCK_PLACEMENT_FAILED = ImmutableStatus.blocking(new ResourceLocation(ICBMConstants.DOMAIN, "action.block.placement.failed"));

    public static IActionStatus MISSING_WORLD = new MissingFieldStatus(new ResourceLocation(ICBMConstants.DOMAIN, "action.error.field.world.missing"), "action", "world");
    public static IActionStatus MISSING_BLOCK_POS = new MissingFieldStatus(new ResourceLocation(ICBMConstants.DOMAIN, "action.error.field.block.pos.missing"), "action", "blockPos");
    public static IActionStatus MISSING_BLOCK_STATE = new MissingFieldStatus(new ResourceLocation(ICBMConstants.DOMAIN, "action.error.field.block.state.missing"), "action", "blockState");
    public static IActionStatus MISSING_VALIDATION = new MissingFieldStatus(new ResourceLocation(ICBMConstants.DOMAIN, "action.error.field.validation.missing"), "action", "validation");

    // </editor-fold>

    // <editor-fold description="good status">
    public static IActionStatus COMPLETED = ImmutableStatus.create(new ResourceLocation(ICBMConstants.DOMAIN, "action.completed"));
    // </editor-fold>

    public static void registerTypes() {

        UNKNOWN_ERROR.registerStatic();
        ENTITY_SPAWN_FAILED.registerStatic();
        EXPLOSION_CANCELED.registerStatic();
        MISSING_WORLD.registerStatic();
        COMPLETED.registerStatic();
        VALIDATION_ERROR.registerStatic();
    }
}
