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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Pre-built action for general purpose implementation where all components are known.
 */
public abstract class PotentialActionImp<SELF extends PotentialActionImp<SELF>> implements IPotentialAction, IActionFieldProvider, ITick, INBTSerializable<NBTTagCompound> {

    private final Map<ActionField, Supplier> fieldAccessors = new HashMap();

    @Getter
    private ICondition preCheck;

    public SELF withCondition(ICondition check) {
        this.preCheck = check;
        return (SELF) this;
    }

    public <T> SELF field(ActionField<T> field, Supplier<T> supplier) {
        if(!fieldAccessors.containsKey(field)) {
            fieldAccessors.put(field, supplier);
        }
        return (SELF)this;
    }

    @Nonnull
    @Override
    public IActionStatus checkAction(World world, double x, double y, double z, @Nullable IActionCause cause) {
        // Edge case: lazy init failed to get action data
        if(this.getActionData() == null) {
            return ActionResponses.MISSING_ACTION_DATA;
        }
        return preCheck == null ? ActionResponses.READY : preCheck.getCondition();
    }

    public IActionStatus doAction(World world, BlockPos pos, @Nullable IActionCause cause) {
        return doAction(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, cause);
    }

    @Nonnull
    @Override
    public IActionStatus doAction(World world, double x, double y, double z, @Nullable IActionCause cause) {
        final IActionStatus preCheckStatus = checkAction(world, x, y, z, cause); //TODO if preCheck is a special type use in cause-by chain. Example: timer
        if(preCheckStatus.isType(ActionStatusTypes.BLOCKING)) {
            return preCheckStatus;
        }
        if(preCheck != null) {
            preCheck.reset();
        }
        final IActionSource actionSource = new ActionSource(world, new Vec3d(x, y, z), cause);
        return ICBMClassicAPI.ACTION_LISTENER.runAction(getActionData().create(world, x, y, z, actionSource, this));
    }

    @Override
    public <T> T getValue(ActionField<T> key) {
        if(fieldAccessors.containsKey(key)) {
            return key.cast(fieldAccessors.get(key).get());
        }
        return null;
    }

    @Override
    public Collection<ActionField> getFields() {
        return fieldAccessors.keySet();
    }

    @Override
    public <T> boolean hasField(ActionField<T> key) {
        return fieldAccessors.containsKey(key);
    }

    @Override
    public void update(int tick, boolean isServer) {
        if(isServer && preCheck != null) {
            preCheck.onTick();
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return SAVE_LOGIC.save(this);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        SAVE_LOGIC.load(this, nbt);
    }

    private static final NbtSaveHandler<PotentialActionImp> SAVE_LOGIC = new NbtSaveHandler<PotentialActionImp>()
        .mainRoot()
        /* */.nodeBuildableObject("pre_check", () -> ICBMClassicAPI.CONDITION_REGISTRY, PotentialActionImp::getPreCheck, PotentialActionImp::setPreCheck)
        .base();
}
