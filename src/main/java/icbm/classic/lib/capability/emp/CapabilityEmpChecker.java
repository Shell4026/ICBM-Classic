package icbm.classic.lib.capability.emp;

import icbm.classic.api.actions.IAction;
import icbm.classic.api.caps.IEMPReceiver;
import icbm.classic.api.explosion.IBlast;
import net.minecraft.world.World;

/**
 * Basic version of the capability that acts as a placeholder
 *
 *
 * Created by Dark(DarkGuardsman, Robin) on 3/12/2018.
 */
public class CapabilityEmpChecker implements IEMPReceiver
{
    public int timesHitByEMP = 0;

    @Override
    public float applyEmpAction(World world, double x, double y, double z, IAction emp_blast, float power, boolean doAction)
    {
        if (doAction)
        {
            timesHitByEMP++;
        }
        return power;
    }
}
