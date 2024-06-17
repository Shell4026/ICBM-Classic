package icbm.classic.content.blast.thread;

import icbm.classic.content.blast.Blast;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Used for small explosions.
 *
 * @author Calclavia
 */
@Deprecated
public class ThreadSmallExplosion extends ThreadExplosion
{
    public ThreadSmallExplosion(Blast blast, int radius, Entity source)
    {
        super(blast, radius, 0, source);
    }

    @Override
    public void doRun(World world, Vec3d center)
    {
        for (int x = 0; x < this.radius; ++x)
        {
            for (int y = 0; y < this.radius; ++y)
            {
                for (int z = 0; z < this.radius; ++z)
                {
                    if (kill)
                    {
                        return;
                    }
                    if (x == 0 || x == this.radius - 1 || y == 0 || y == this.radius - 1 || z == 0 || z == this.radius - 1)
                    {
                        double xStep = x / (this.radius - 1.0F) * 2.0F - 1.0F;
                        double yStep = y / (this.radius - 1.0F) * 2.0F - 1.0F;
                        double zStep = z / (this.radius - 1.0F) * 2.0F - 1.0F;
                        double diagonalDistance = Math.sqrt(xStep * xStep + yStep * yStep + zStep * zStep);
                        xStep /= diagonalDistance;
                        yStep /= diagonalDistance;
                        zStep /= diagonalDistance;
                        float power = this.radius * (0.7F + this.world.rand.nextFloat() * 0.6F);
                        double var15 = position.x;
                        double var17 = position.y;
                        double var19 = position.z;

                        for (float var21 = 0.3F; power > 0.0F; power -= var21 * 0.75F)
                        {
                            BlockPos targetPosition = new BlockPos(var15, var17, var19);

                            if(!world.isBlockLoaded(targetPosition)) //TODO: find better fix for non main thread loading
                                continue;

                            BlockState state = this.world.getBlockState(targetPosition);
                            Block block = state.getBlock();

                            if (!block.isAir(state, world, targetPosition))
                            {
                                float resistance = 0;

                                if (state.getBlockHardness(world, targetPosition) < 0)
                                {
                                    break;
                                }
                                else
                                {
                                    resistance = block.getExplosionResistance(state, world, targetPosition, source, blast);
                                }
                                // TODO rather than remove power divert a percentage to the
                                // sides, and then calculate how much is absorbed by the block
                                power -= resistance;
                            }

                            if (power > 0.0F)
                            {
                                this.blast.addThreadResult(targetPosition);
                            }

                            var15 += xStep * var21;
                            var17 += yStep * var21;
                            var19 += zStep * var21;
                        }
                    }
                }
            }
        }
    }
}
