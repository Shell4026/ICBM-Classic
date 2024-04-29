package icbm.classic.lib.radio.messages;

import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.api.missiles.IMissile;
import icbm.classic.api.radio.messages.IIncomingMissileMessage;
import icbm.classic.content.blocks.launcher.status.LaunchedWithMissile;
import icbm.classic.content.missile.entity.anti.EntitySurfaceToAirMissile;
import lombok.Data;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

@Data
public class IncomingMissileMessage implements IIncomingMissileMessage {
    private final String channel;
    private final IMissile missile;
    private final boolean trigger;

    @Override
    public Vec3d getTarget() {
        return Optional.ofNullable(missile).map(IMissile::getVec3d).orElse(null);
    }

    @Override
    public boolean shouldTrigger() {
        return trigger;
    }

    @Override
    public void onTriggerCallback(IActionStatus status) {
        if(status instanceof LaunchedWithMissile) {
            final IMissile firedMissile = ((LaunchedWithMissile) status).getMissile();
            if(firedMissile != null && firedMissile.getMissileEntity() instanceof EntitySurfaceToAirMissile) {
               final EntitySurfaceToAirMissile sam = (EntitySurfaceToAirMissile) firedMissile.getMissileEntity();
               sam.scanLogic.setCurrentTarget(missile.getMissileEntity());
            }
        }
    }
}
