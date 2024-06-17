package icbm.classic.lib.energy.system;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles wrapping power system support for internal checks and calls
 *
 *
 * Created by Dark(DarkGuardsman, Robin) on 3/13/2018.
 */
public final class EnergySystem
{
    private final static IEnergySystem NULL_SYSTEM = new EnergySystemNull();
    private final static List<IEnergySystem> energySystems = new ArrayList();

    public static IEnergySystem getSystem(TileEntity tile, Direction side)
    {
        return getSystemForObject(tile, side);
    }

    public static IEnergySystem getSystem(Entity entity, Direction side)
    {
        return getSystemForObject(entity, side);
    }

    public static IEnergySystem getSystem(ItemStack item, Direction side)
    {
        return getSystemForObject(item, side);
    }

    public static boolean isEnergyItem(ItemStack stack) {
        return isEnergyItem(stack, null);
    }

    public static boolean isEnergyItem(ItemStack stack, Direction side) {
        return energySystems.stream().anyMatch(system -> system.canSupport(stack, side));
    }

    private static IEnergySystem getSystemForObject(Object object, Direction side)
    {
        for (IEnergySystem system : energySystems)
        {
            if (system.canSupport(object, side))
            {
                return system;
            }
        }
        return NULL_SYSTEM;
    }

    public static void register(IEnergySystem energySystem)
    {
        if(energySystem != null)
        {
            energySystems.add(energySystem);
        }
    }
}
