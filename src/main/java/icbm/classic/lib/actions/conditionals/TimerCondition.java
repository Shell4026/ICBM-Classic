package icbm.classic.lib.actions.conditionals;

import icbm.classic.ICBMConstants;
import icbm.classic.api.actions.conditions.ICondition;
import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.lib.actions.status.ActionResponses;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

@NoArgsConstructor
@AllArgsConstructor
public class TimerCondition implements ICondition, INBTSerializable<NBTTagInt> {
    public static final ResourceLocation REG_NAME = new ResourceLocation(ICBMConstants.DOMAIN, "timer");

    private int tick = 0;

    @Override
    public void onTick()
    {
       tick--;
    }

    @Override
    public IActionStatus getCondition() {
        return tick <= 0 ? ActionResponses.READY : ActionResponses.WAITING; //TODO replace waiting with status on time left
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryKey() {
        return REG_NAME;
    }

    @Override
    public NBTTagInt serializeNBT() {
        return new NBTTagInt(tick);
    }

    @Override
    public void deserializeNBT(NBTTagInt nbt) {
        tick = nbt.getInt();
    }
}
