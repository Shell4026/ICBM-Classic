package icbm.classic.lib.actions.conditionals;

import icbm.classic.ICBMConstants;
import icbm.classic.api.actions.cause.IActionCause;
import icbm.classic.api.actions.conditions.IConditionCause;
import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.lib.actions.status.ActionResponses;
import icbm.classic.lib.saving.NbtSaveHandler;
import lombok.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

@NoArgsConstructor
@AllArgsConstructor
public class TimerCondition extends Condition implements IConditionCause {
    public static final ResourceLocation REG_NAME = new ResourceLocation(ICBMConstants.DOMAIN, "timer");

    @Getter @Setter
    private int target = 0;

    @Getter @Setter(AccessLevel.PRIVATE)
    private int current = 0;

    @Override
    public void onTick()
    {
        if(current < target) {
            current++;
        }
    }

    @Override
    public void reset() {
        this.current = 0;
    }

    @Override
    public IActionStatus getCondition() {
        return target <= 0 ? ActionResponses.READY : ActionResponses.WAITING; //TODO replace waiting with status on time left
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryKey() {
        return REG_NAME;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return SAVE_LOGIC.save(this, super.serializeNBT());
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        SAVE_LOGIC.load(this, nbt);
    }

    private static final NbtSaveHandler<TimerCondition> SAVE_LOGIC = new NbtSaveHandler<TimerCondition>()
        .mainRoot()
        /* */.nodeInteger("tick_current", TimerCondition::getCurrent, TimerCondition::setCurrent)
        /* */.nodeInteger("tick_target", TimerCondition::getTarget, TimerCondition::setTarget)
        .base();

    @Override
    public IActionCause getCause() {
        return null;
    }
}
