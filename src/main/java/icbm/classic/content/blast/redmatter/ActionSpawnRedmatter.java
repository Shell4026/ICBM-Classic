package icbm.classic.content.blast.redmatter;

import icbm.classic.api.actions.IActionData;
import icbm.classic.api.actions.cause.IActionSource;
import icbm.classic.api.actions.data.ActionField;
import icbm.classic.api.actions.data.ActionFields;
import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.config.blast.ConfigBlast;
import icbm.classic.lib.actions.ActionBase;
import icbm.classic.lib.actions.status.ActionResponses;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Action to spawn a redmatter entity
 */
public class ActionSpawnRedmatter extends ActionBase
{
    private Float size = null;

    public ActionSpawnRedmatter(World world, Vec3d position, IActionSource source, IActionData actionData) {
        super(world, position, source, actionData);
    }

    @Override
    public <VALUE, TAG extends NBTBase> void setValue(ActionField<VALUE, TAG> key, VALUE value) {
        if(key == ActionFields.AREA_SIZE) {
            size = ActionFields.AREA_SIZE.cast(value);
        }
    }

    @Nonnull
    @Override
    public IActionStatus doAction() {
        //Build entity
        final EntityRedmatter entityRedmatter = new EntityRedmatter(getWorld());
        entityRedmatter.setPosition(getPosition().x, getPosition().y, getPosition().z);
        entityRedmatter.setBlastSize(ConfigBlast.redmatter.DEFAULT_SIZE);
        entityRedmatter.setBlastMaxSize(ConfigBlast.redmatter.MAX_SIZE);

        if(size != null) {
            entityRedmatter.setBlastSize(size);
        }

        //Attempt to spawn
        if (getWorld().spawnEntity(entityRedmatter))
        {
            return ActionResponses.COMPLETED;
        }
        return ActionResponses.ENTITY_SPAWN_FAILED;
    }
}
