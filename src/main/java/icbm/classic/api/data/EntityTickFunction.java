package icbm.classic.api.data;

import net.minecraft.entity.Entity;

/**
 *
 * Created by Dark(DarkGuardsman, Robin) on 1/7/19.
 */
@FunctionalInterface
public interface EntityTickFunction
{
    void onTick(Entity entity, int tick);
}
