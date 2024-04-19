package icbm.classic.lib.actions;

import icbm.classic.ICBMClassic;
import icbm.classic.ICBMConstants;
import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.actions.IActionData;
import icbm.classic.api.actions.IPotentialAction;
import icbm.classic.api.actions.cause.IActionCause;
import icbm.classic.api.actions.cause.IActionSource;
import icbm.classic.api.actions.conditions.ICondition;
import icbm.classic.api.actions.data.ActionField;
import icbm.classic.api.actions.data.IActionFieldProvider;
import icbm.classic.api.actions.status.ActionStatusTypes;
import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.content.missile.logic.source.ActionSource;
import icbm.classic.lib.actions.status.ActionResponses;
import icbm.classic.lib.data.LazyBuilder;
import icbm.classic.lib.saving.NbtSaveHandler;
import icbm.classic.lib.tile.ITick;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * General purpose action for loading into entities, capabilities, and tiles. In which
 * the action triggered and condition are customizable.
 */
public final class PotentialAction extends PotentialActionImp<PotentialAction> {

    public static final ResourceLocation REG_NAME = new ResourceLocation(ICBMConstants.DOMAIN, "basic");

    @Setter() @Getter @Accessors(chain = true)
    private IActionData actionData;

    @Override
    public NBTTagCompound serializeNBT() {
        return SAVE_LOGIC.save(this, super.serializeNBT());
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        SAVE_LOGIC.load(this, nbt);
    }

    private static final NbtSaveHandler<PotentialAction> SAVE_LOGIC = new NbtSaveHandler<PotentialAction>()
        .mainRoot()
        /* */.nodeBuildableObject("action_data", () -> ICBMClassicAPI.ACTION_REGISTRY, PotentialAction::getActionData, PotentialAction::setActionData)
        .base();

    @Nonnull
    @Override
    public ResourceLocation getRegistryKey() {
        return REG_NAME;
    }
}
