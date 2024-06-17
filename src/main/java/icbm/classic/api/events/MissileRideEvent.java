package icbm.classic.api.events;

import icbm.classic.api.missiles.IMissile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class MissileRideEvent extends Event
{
    public final IMissile missile;
    public final PlayerEntity player;

    public MissileRideEvent(IMissile missile, PlayerEntity player)
    {
        this.missile = missile;
        this.player = player;
    }

    /**
     * Called right before a player starts to ride a missile.
     * Cancel this event to disallow the player to ride the missile.
     */
    @Cancelable
    public static class Start extends MissileRideEvent
    {
        public Start(IMissile missile, PlayerEntity player)
        {
            super(missile, player);
        }
    }

    /**
     * Called right before a player stops to ride a missile.
     * Cancel this event to disallow the player to dismount the missile.
     */
    @Cancelable
    public static class Stop extends MissileRideEvent
    {
        public Stop(IMissile missile, PlayerEntity player)
        {
            super(missile, player);
        }
    }
}
