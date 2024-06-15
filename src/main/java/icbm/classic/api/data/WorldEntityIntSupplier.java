package icbm.classic.api.data;


import net.minecraft.entity.Entity;

/**
 * Created by Dark(DarkGuardsman, Robin) on 1/7/19.
 */
@FunctionalInterface
public interface WorldEntityIntSupplier
{
    /**
     * Calculates or retrieves an int based on the entity
     *
     * @param entity
     * @return
     */
    int get(Entity entity);
}
