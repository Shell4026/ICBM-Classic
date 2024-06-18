package icbm.classic.content.missile.entity;

import icbm.classic.api.actions.IAction;
import icbm.classic.api.caps.IEMPReceiver;
import icbm.classic.api.missiles.IMissile;
import icbm.classic.config.ConfigEMP;
import icbm.classic.content.missile.entity.anti.EntitySurfaceToAirMissile;
import icbm.classic.content.missile.entity.explosive.EntityExplosiveMissile;
import icbm.classic.content.missile.entity.explosive.EntityMissileActionable;
import icbm.classic.content.missile.logic.flight.DeadFlightLogic;
import icbm.classic.content.missile.logic.source.ActionSource;
import icbm.classic.content.missile.logic.source.cause.CausedByBlock;
import icbm.classic.content.missile.logic.source.cause.EntityCause;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 *
 * Created by Dark(DarkGuardsman, Robin) on 3/12/2018.
 */
public class CapabilityEmpMissile implements IEMPReceiver
{
    final IMissile missile;
    public CapabilityEmpMissile(IMissile missile)
    {
       this.missile = missile;
    }

    @Override
    public float applyEmpAction(World world, double x, double y, double z, IAction emp_blast, float power, boolean doAction)
    {
        if(ConfigEMP.missiles.enabled && missile.getMissileEntity() != null && missile.getMissileEntity().isEntityAlive())
        {
            if (doAction)
            {
                if(world.rand.nextFloat() <= ConfigEMP.missiles.missileKillChance) {
                    missile.getMissileEntity().setDead();

                    // Boom
                    if(world.rand.nextFloat() <= ConfigEMP.missiles.engineFullBlowChance) {
                        world.createExplosion(missile.getMissileEntity(), x, y, z, ConfigEMP.missiles.engineFullBlowStrength, false);
                        // TODO drop fragments of missile that can still cause damage
                    }
                    //TODO else drop item or dead entity?
                }
                // Fry guidance systems
                else if(world.rand.nextFloat() <= ConfigEMP.missiles.engineKillChance) {
                    missile.setFlightLogic(new DeadFlightLogic(0));
                }
                // Trigger firing circuits
                else if(world.rand.nextFloat() <= ConfigEMP.missiles.missileTriggerChance) {
                    if(missile.getMissileEntity() instanceof EntityExplosiveMissile) {
                        ((EntityExplosiveMissile) missile.getMissileEntity()).explosive
                            .doExplosion(x, y, z, emp_blast.getSource()); //TODO encode source to note emp death
                    }
                    else if(missile.getMissileEntity() instanceof EntityMissileActionable) {
                        ((EntityMissileActionable) missile.getMissileEntity()).getMainAction().
                            doAction(world, x, y, z, emp_blast.getSource().getCause()); //TODO encode source to note emp death
                    }
                    else if(missile.getMissileEntity() instanceof EntitySurfaceToAirMissile) {
                        world.createExplosion(missile.getMissileEntity(), x, y, z, ConfigEMP.missiles.engineFullBlowStrength, false);
                    }
                }
                // TODO chance for missile to fry trigger but still land
            }
        }
        return power;
    }
}
