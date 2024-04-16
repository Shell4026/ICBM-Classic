package icbm.classic.api.actions.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ActionFields {
    /** General area size factor */
    public static final ActionField<Float> AREA_SIZE = new ActionField<Float>("area.size", Float.class);
    /** Way to access host entity, usually provided by the entity itself */
    public static final ActionField<Entity> HOST_ENTITY = new ActionField<Entity>("host.entity", Entity.class);
    /** True if system has impacted something. Usually being entity has impacted ground or another entity. */
    public static final ActionField<Boolean> IMPACTED = new ActionField<Boolean>("impacted", Boolean.class);
    /** Position of the current target or aim */
    public static final ActionField<Vec3d> TARGET_POSITION = new ActionField<>("target.vec3d", Vec3d.class);
    /** Position of the host */
    public static final ActionField<Vec3d> HOST_POSITION = new ActionField<>("host.vec3d", Vec3d.class);
}
