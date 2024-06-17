package icbm.classic.lib;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Dark(DarkGuardsman, Robin) on 1/9/19.
 */
public abstract class CapabilityPrefab implements ICapabilitySerializable<CompoundNBT>
{

    public abstract boolean isCapability(@Nonnull Capability<?> capability);

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing)
    {
        return isCapability(capability);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
    {
        if (isCapability(capability))
        {
            return (T) this;
        }
        return null;
    }


    @Override
    public final CompoundNBT serializeNBT()
    {
        CompoundNBT tagCompound = new CompoundNBT();
        save(tagCompound);
        return tagCompound;
    }

    @Override
    public final void deserializeNBT(CompoundNBT nbt)
    {
        if (nbt != null && !nbt.hasNoTags())
        {
            load(nbt);
        }
    }

    protected void save(CompoundNBT tag)
    {

    }

    protected void load(CompoundNBT tag)
    {

    }
}
