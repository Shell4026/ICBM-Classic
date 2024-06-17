package icbm.classic.lib.capability.launcher;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.caps.IMissileHolder;
import icbm.classic.api.missiles.ICapabilityMissileStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

public class CapabilityMissileHolder implements IMissileHolder
{
    private final IItemHandlerModifiable inventory;
    private final int slot;

    public CapabilityMissileHolder(IItemHandlerModifiable inventory, int slot)
    {
        this.inventory = inventory;
        this.slot = slot;
    }

    @Override
    public ItemStack getMissileStack()
    {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertMissileStack(ItemStack stack, boolean simulate)
    {
        if(canSupportMissile(stack)) {
            return inventory.insertItem(slot, stack, simulate);
        }
        return null;
    }

    @Override
    public boolean consumeMissile() {
        if(hasMissile()) {
            final ICapabilityMissileStack missileStack = getMissileStack().getCapability(ICBMClassicAPI.MISSILE_STACK_CAPABILITY, null);
            if(missileStack != null) {
                inventory.setStackInSlot(slot, missileStack.consumeMissile());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canSupportMissile(ItemStack stack)
    {
        return inventory.isItemValid(slot, stack);
    }

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IMissileHolder.class, new Capability.IStorage<IMissileHolder>()
            {
                @Nullable
                @Override
                public INBT writeNBT(Capability<IMissileHolder> capability, IMissileHolder instance, Direction side)
                {
                    return null;
                }

                @Override
                public void readNBT(Capability<IMissileHolder> capability, IMissileHolder instance, Direction side, INBT nbt)
                {

                }
            },
            () -> new CapabilityMissileHolder(null, 0));
    }
}
