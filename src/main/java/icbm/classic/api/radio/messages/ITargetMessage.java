package icbm.classic.api.radio.messages;

import icbm.classic.api.radio.IRadioMessage;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Packet containing targeting information
 */
public interface ITargetMessage extends IRadioMessage {

    /**
     * Target position, may be pull from other
     * data so cache when possible.
     *
     * Will only return null if target was lost
     *
     * @return position of target
     */
    @Nullable
    Vec3d getTarget();

    /**
     * Possible entity target, not all target messages
     * will contain the entity.
     *
     * @return entity being targeted
     */
    @Nullable
    default Entity getTargetEntity() {
        return null;
    }
}
