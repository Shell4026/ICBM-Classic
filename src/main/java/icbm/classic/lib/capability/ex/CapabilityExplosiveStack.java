package icbm.classic.lib.capability.ex;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.caps.IExplosive;
import icbm.classic.api.explosion.IBlast;
import icbm.classic.api.reg.IExplosiveCustomization;
import icbm.classic.api.reg.IExplosiveData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Used by any item that has an explosive capability
 * Created by Dark(DarkGuardsman, Robin) on 1/7/19.
 */
public class CapabilityExplosiveStack implements IExplosive, ICapabilitySerializable<NBTTagCompound>
{
    private final ItemStack stack;
    private final List<IExplosiveCustomization> customizationList = new ArrayList();

    public CapabilityExplosiveStack(ItemStack stack)
    {
        this.stack = stack;
    }

    protected int getExplosiveID()
    {
        if(stack == null)
        {
            return 0;
        }
        return stack.getItemDamage(); //TODO replace meta usage for 1.14 update
    }

    @Nullable
    @Override
    public IExplosiveData getExplosiveData()
    {
        return ICBMClassicAPI.EXPLOSIVE_REGISTRY.getExplosiveData(getExplosiveID());
    }

    @Override
    public void applyCustomizations(IBlast blast) {
        customizationList.forEach(c -> c.apply(getExplosiveData(), blast));
    }

    @Override
    public void addCustomization(IExplosiveCustomization customization) {
        customizationList.add(customization);
    }

    @Override
    public void collectInformation(Consumer<String> collector){
        //TODO debug explosive id and name if F3+something keybind is enabled
        customizationList.forEach(c -> c.collectCustomizationInformation(collector));
    }

    @Nullable
    @Override
    public ItemStack toStack()
    {
        if (stack == null)
        {
            return ItemStack.EMPTY;
        }

        final ItemStack re = stack.copy();
        //TODO save customizations
        re.setCount(1);
        return re;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound save = new NBTTagCompound();
        save.setTag("customizations", ICBMClassicAPI.EXPLOSIVE_CUSTOMIZATION_REGISTRY.save(customizationList));
        return save;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        if(nbt.hasKey("customizations")) {
            ICBMClassicAPI.EXPLOSIVE_CUSTOMIZATION_REGISTRY.load(nbt.getTagList("customizations", 10), customizationList);
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == ICBMClassicAPI.EXPLOSIVE_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == ICBMClassicAPI.EXPLOSIVE_CAPABILITY)
        {
            return ICBMClassicAPI.EXPLOSIVE_CAPABILITY.cast(this);
        }
        return null;
    }
}
