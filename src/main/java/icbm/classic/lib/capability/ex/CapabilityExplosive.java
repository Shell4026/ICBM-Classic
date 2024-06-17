package icbm.classic.lib.capability.ex;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.ICBMClassicHelpers;
import icbm.classic.api.caps.IExplosive;
import icbm.classic.api.explosion.IBlast;
import icbm.classic.api.reg.IExplosiveCustomization;
import icbm.classic.api.reg.IExplosiveData;
import icbm.classic.lib.NBTConstants;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Dark(DarkGuardsman, Robin) on 1/7/19.
 */
public class CapabilityExplosive implements IExplosive, ICapabilitySerializable<CompoundNBT>
{
    public int explosiveID; //TODO change over to resource location or include in save to check for issues using ID only for in memory

    public CapabilityExplosive()
    {
    }

    public CapabilityExplosive(int id)
    {
        this.explosiveID = id;
    }

    @Nullable
    @Override
    public IExplosiveData getExplosiveData()
    {
        return ICBMClassicHelpers.getExplosive(explosiveID, false);
    }

    @Override
    public void applyCustomizations(IBlast blast) {

    }

    @Override
    public void addCustomization(IExplosiveCustomization customization) {

    }

    @Nullable
    @Override
    public ItemStack toStack()
    {
        return null;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing)
    {
        return capability == ICBMClassicAPI.EXPLOSIVE_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
    {
        if (capability == ICBMClassicAPI.EXPLOSIVE_CAPABILITY)
        {
            return (T) this;
        }
        return null;
    }

    @Override
    public final CompoundNBT serializeNBT()
    {
        final CompoundNBT tagCompound = new CompoundNBT();
        serializeNBT(tagCompound);

        tagCompound.setInteger(NBTConstants.EXPLOSIVE_ID, explosiveID);
        return tagCompound;
    }

    protected void serializeNBT(CompoundNBT tag)
    {

    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        if (nbt.hasKey(NBTConstants.EXPLOSIVE_ID))
        {
            explosiveID = nbt.getInteger(NBTConstants.EXPLOSIVE_ID);
        }
    }

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IExplosive.class, new Capability.IStorage<IExplosive>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IExplosive> capability, IExplosive instance, Direction side)
            {
                if (instance instanceof CapabilityExplosive)
                {
                    return ((CapabilityExplosive) instance).serializeNBT();
                }
                return null;
            }

            @Override
            public void readNBT(Capability<IExplosive> capability, IExplosive instance, Direction side, NBTBase nbt)
            {
                if (instance instanceof CapabilityExplosive && nbt instanceof CompoundNBT)
                {
                    ((CapabilityExplosive) instance).deserializeNBT((CompoundNBT) nbt);
                }
            }
        },
        () -> new CapabilityExplosive(-1));
    }
}
