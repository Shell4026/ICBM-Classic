package icbm.classic.lib.explosive;

import icbm.classic.ICBMClassic;
import icbm.classic.ICBMConstants;
import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.actions.cause.IActionSource;
import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.api.caps.IExplosive;
import icbm.classic.api.explosion.IBlast;
import icbm.classic.api.explosion.IBlastInit;
import icbm.classic.api.reg.IExplosiveData;
import icbm.classic.content.blast.Blast;
import icbm.classic.content.actions.status.MissingFieldStatus;
import icbm.classic.lib.transform.vector.Pos;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Simple handler to track blasts in order to disable or remove
 * <p>
 * <p>
 * Created by Dark(DarkGuardsman, Robert) on 4/9/2018.
 */
@Mod.EventBusSubscriber(modid = ICBMConstants.DOMAIN)
public class ExplosiveHandler
{
    public static final ArrayList<IBlast> activeBlasts = new ArrayList();

    public static void add(Blast blast)
    {
        activeBlasts.add(blast);
    }

    public static void remove(Blast blast)
    {
        activeBlasts.remove(blast);
    }

    @SubscribeEvent
    public static void worldUnload(WorldEvent.Unload event)
    {
        if (!event.getWorld().isRemote)
        {
            final int dim = event.getWorld().provider.getDimension();
            activeBlasts.stream()
                    .filter(blast -> !blast.hasWorld() || blast.world().provider.getDimension() == dim)
                    .forEach(IBlast::clearBlast);
        }
    }

    /**
     * Called to remove blasts near the location
     *
     * @param world = position
     * @param x     - position
     * @param y     - position
     * @param z     - position
     * @param range - distance from position, less than zero will turn into global
     * @return number of blasts removed
     */
    public static int removeNear(World world, double x, double y, double z, double range)
    {
        final Pos pos = new Pos(x, y, z);

        //Collect blasts marked for removal
        final List<IBlast> toRemove = ExplosiveHandler.activeBlasts.stream()
                .filter(blast -> blast.world() == world)
                .filter(blast -> range < 0 || range > 0 && range > pos.distance(blast))
                .collect(Collectors.toList());

        //Do removals
        activeBlasts.removeAll(toRemove);
        toRemove.forEach(IBlast::clearBlast);

        return toRemove.size();
    }

    public static IActionStatus createExplosion(Entity cause, World world, double x, double y, double z, @Nonnull IExplosiveData explosiveData, IActionSource source, float scale, Consumer<IBlast> customizer)
    {
        final IBlastInit blast = explosiveData.create(world, x, y, z, source).scaleBlast(scale).setBlastSource(cause).setExplosiveData(explosiveData).setActionSource(source);
        if(customizer != null) {
            customizer.accept(blast);
        }
        return logEventThenRespond(cause, world, x, y, z, explosiveData, scale, blast.buildBlast().doAction());
    }

    private static IActionStatus logEventThenRespond(Entity cause, World world, double x, double y, double z, IExplosiveData explosiveData, float scale, IActionStatus blastResponse)
    {
        final String explosiveName = explosiveData == null ? "null" : explosiveData.getRegistryKey().toString();
        final String entitySource = cause != null ? Integer.toString(cause.getEntityId()) : "null";

        // TODO make optional via config
        // TODO log to ICBM file separated from main config
        // TODO offer hook for database logging
        final String formatString = "Explosion[%s] | Scale(x%,.1f) | EntitySource(%s) | Impacted (%,.1fx %,.1fy %,.1fz %sd) | Status(%s)";
        final String formattedMessage = String.format(formatString,
            explosiveName,
            scale,
            entitySource,
            x,
            y,
            z,
            world.provider.getDimension(),
            blastResponse.message().getFormattedText()
        );

        if (blastResponse.isError())
        {
            ICBMClassic.logger().error(formattedMessage);
        }
        else
        {
            ICBMClassic.logger().info(formattedMessage);
        }
        return blastResponse;
    }
}
