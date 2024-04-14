package icbm.classic.lib.actions;

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
import java.util.*;
import java.util.function.Supplier;

/**
 * Pre-built action for general purpose implementation where action is known
 */
public final class PotentialActionKnown extends PotentialActionImp {

    private final LazyBuilder<IActionData> actionData;

    public PotentialActionKnown(ResourceLocation key) {
        this.actionData = new LazyBuilder<>(() -> ICBMClassicAPI.ACTION_REGISTRY.getOrBuild(key));
    }

    @Nonnull
    @Override
    public IActionData getActionData() {
        return actionData.get();
    }
}
