package icbm.classic.api.missiles.projectile;

import icbm.classic.api.data.meta.MetaTag;
import lombok.Data;
import net.minecraft.util.ResourceLocation;

@Data
public final class ProjectileTypes {

    /** All projectiles */
    public static final MetaTag TYPE_PROJECTILE = MetaTag.create(new ResourceLocation("icbmclassic","projectile"));

    /** Applied to types that act as holders for other objects or entities... think landing rockets or parachutes */
    public static final MetaTag TYPE_HOLDER = MetaTag.create(TYPE_PROJECTILE, "holder");

    /** Applied to projectiles that can be thrown or projected by a source */
    public static final MetaTag TYPE_THROWABLE = MetaTag.create(TYPE_PROJECTILE, "throwable");
    /** Missiles that use the capability {@link icbm.classic.api.missiles.IMissile} */
    public static final MetaTag TYPE_MISSILE = MetaTag.create(TYPE_PROJECTILE, "missile");
    /** Projectiles that are explosive and use the capability {@link icbm.classic.api.caps.IExplosive} */
    public static final MetaTag TYPE_EXPLOSIVE = MetaTag.create(TYPE_PROJECTILE, "explosive");
}
