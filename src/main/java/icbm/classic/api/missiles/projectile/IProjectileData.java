package icbm.classic.api.missiles.projectile;

import icbm.classic.api.data.meta.ITypeTaggable;
import icbm.classic.api.data.meta.MetaTag;
import icbm.classic.api.reg.obj.IBuildableObject;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Information about a projectile, usually wrapper for an entity or entity spawn system. Provides
 * the type of projectile, what systems it can work with, and how to place the projectile into the world.
 *
 * Projectile system exists as a way to abstract entity from system. Allowing content, such as cluster, to
 * list supported projectiles and store them in a safe data structure. Where the entity can be spawned when
 * needed without knowing what it is or how it works.
 *
 * @param <E> created from the projectile data
 */
public interface IProjectileData<E extends Entity>  extends IBuildableObject, ITypeTaggable {

    //TODO add a way to check size, this way we can limit weapon systems from using extremely large projectiles if too small... share size data with radar

    /**
     * Called to generate a new projectile entity
     *
     * @param world to spawn inside
     * @param allowItemPickup true to allow entity to be collected, such as picking up arrows
     * @return entity to spawn
     */
    E newEntity(World world, boolean allowItemPickup); //TODO replace with IActionSolution

    /**
     * Called after the entity has been added to the world. Useful
     * for adding riding entities or customize based on position.
     *
     * Usually at this point the entity has it's set position, motion, and rotations. It
     * is recommended to not change this information as it can break interactions.
     *
     * @param entity created and added to the world
     * @param source that created the entity, may not always be present
     * @param hand used to spawn entity, for non-humanoid this will always be main hand
     */
    default void onEntitySpawned(@Nonnull E entity, @Nullable Entity source, @Nullable EnumHand hand) { //TODO consider moving to a IAction

    }

}
