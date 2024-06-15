package icbm.classic.api.data;

import net.minecraft.world.World;

/**
 * Created by Dark(DarkGuardsman, Robin) on 1/7/19.
 */
@FunctionalInterface
public interface WorldPosIntSupplier
{
    /**
     * Calculates or retrieves an int based on the world and position
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @return
     */
    int get(World world, double x, double y, double z);
}
