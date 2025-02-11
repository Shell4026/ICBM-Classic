package icbm.classic.api.reg.content;

import icbm.classic.api.data.EntityTickFunction;
import icbm.classic.api.data.WorldEntityIntSupplier;
import icbm.classic.api.reg.IExplosiveData;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Dark(DarkGuardsman, Robin) on 9/7/2019.
 */
@Deprecated //Will replace at some point with settings reference from IExplosiveData or IExplosive
public interface IExFuseEntityRegistry
{
    /**
     * Called to set a supplier that will be used to define the fuse time
     * of the explosive.
     * <p>
     * Do not use this in place of normal events, this is designed to add logic for
     * specific block types.
     *
     * @param exName   - explosive to register with
     * @param supplier - function that will be called each tick
     */
    void setFuseSupplier(ResourceLocation exName, WorldEntityIntSupplier supplier);

    /**
     * Called to set a function to invoke each tick of an explosive block's fuse.
     * Use this to create interesting effects for unique explosives
     * <p>
     * Do not use this in place of normal events, this is designed to add logic for
     * specific block types.
     *
     * @param exName   - explosive to register with
     * @param function - function that will be called each tick
     */
    void setFuseTickListener(ResourceLocation exName, EntityTickFunction function);

    /**
     * Called by objects to tick the fuse for the explosive
     *
     * @param entity       - entity that is the explosive
     * @param explosiveData  - explosive id
     * @param fuseTimeLeft - ticks existed
     */
    void tickFuse(Entity entity, IExplosiveData explosiveData, int fuseTimeLeft);

    /**
     * Gets the starting fuse time
     *
     * @param entity      - entity that is the explosive
     * @param explosiveData - explosive id
     * @return ticks
     */
    int getFuseTime(Entity entity, IExplosiveData explosiveData);
}
