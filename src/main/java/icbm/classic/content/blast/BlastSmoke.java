package icbm.classic.content.blast;

import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.content.actions.status.ActionResponses;
import icbm.classic.content.blast.imp.BlastBase;
import icbm.classic.content.entity.EntitySmoke;

import javax.annotation.Nonnull;

public class BlastSmoke extends BlastBase
{
    @Nonnull
    @Override
    public IActionStatus triggerBlast()
    {
        final EntitySmoke smoke = new EntitySmoke(world());
        smoke.setPosition(x(), y(), z());
        if(world().spawnEntity(smoke))
        {
            return ActionResponses.COMPLETED;
        }
        return ActionResponses.ENTITY_SPAWN_FAILED;
    }
}
