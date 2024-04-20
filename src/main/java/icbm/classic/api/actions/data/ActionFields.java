package icbm.classic.api.actions.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ActionFields {
    /** General area size factor */
    public static ActionField<Float, NBTTagFloat> AREA_SIZE;

    /** RUNTIME_ONLY: Way to access host entity, usually provided by the entity itself */
    public static ActionField<Entity, NBTBase> HOST_ENTITY;

    /** RUNTIME_ONLY: Position of the host */
    public static ActionField<Vec3d, NBTTagCompound> HOST_POSITION;

    /** True if system has impacted something. Usually being entity has impacted ground or another entity. */
    public static ActionField<Boolean, NBTTagByte> IMPACTED;

    /** Position of the current target or aim, or desired target position of an action */
    public static ActionField<Vec3d, NBTTagCompound> TARGET_POSITION;

    /** Motion vector to set */
    public static ActionField<Vec3d, NBTTagCompound> MOTION_VECTOR;

    /** Rotation yaw to set */
    public static ActionField<Float, NBTTagFloat> YAW;

    /** Rotation pitch to set */
    public static ActionField<Float, NBTTagFloat> PITCH;

    /** Entity registry name, for use with {@link net.minecraft.entity.EntityList} */
    public static ActionField<ResourceLocation, NBTTagString> ENTITY_REG_NAME;

    /** Entity save data */
    public static ActionField<NBTTagCompound, NBTTagCompound> ENTITY_DATA;
}
