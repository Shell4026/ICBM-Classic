package icbm.classic.lib.capability.ex;

import icbm.classic.api.caps.IExplosive;
import icbm.classic.api.explosion.IBlast;
import icbm.classic.api.reg.IExplosiveCustomization;
import icbm.classic.api.reg.IExplosiveData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Used by any item that has an explosive capability
 * Created by Dark(DarkGuardsman, Robin) on 1/7/19.
 */
public class CapabilityExplosiveStatic implements IExplosive, INBTSerializable<CompoundNBT>
{
    private final IExplosiveData data;
    private final Supplier<ItemStack> itemStackSupplier;
    private CompoundNBT custom_ex_data;

    public CapabilityExplosiveStatic(IExplosiveData data, Supplier<ItemStack> itemStackSupplier) {
        this.data = data;
        this.itemStackSupplier = itemStackSupplier;
    }

    @Nullable
    @Override
    public IExplosiveData getExplosiveData()
    {
        return data;
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
        return itemStackSupplier.get();
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        //TODO save customizations
        return new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {

    }
}
