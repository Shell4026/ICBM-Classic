package icbm.classic.api.explosion;

import icbm.classic.api.actions.cause.IActionSource;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Simple interface for use in creating blasts
 *
 *
 * Created by Dark(DarkGuardsman, Robert) on 1/3/19.
 */
@FunctionalInterface
public interface IBlastFactory
{
    /**
     * Creates a new blast
     * @return new blast
     */
    @Nonnull
    IBlastInit create(World world, double x, double y, double z, IActionSource source);
}
