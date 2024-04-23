package icbm.classic.content.blast;

import com.builtbroken.jlib.lang.StringHelpers;
import icbm.classic.ICBMClassic;
import icbm.classic.ICBMConstants;
import icbm.classic.api.explosion.IBlastTickable;
import icbm.classic.content.blast.thread.ThreadSmallExplosion;
import icbm.classic.content.blast.threaded.BlastThreaded;
import icbm.classic.content.entity.flyingblock.BlockCaptureData;
import icbm.classic.content.entity.flyingblock.EntityFlyingBlock;
import icbm.classic.content.entity.flyingblock.FlyingBlock;
import icbm.classic.lib.transform.PosDistanceSorter;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.*;
import java.util.function.Consumer;

public class BlastAntiGravitational extends BlastThreaded implements IBlastTickable {

    public static final int RUNNING_TICKS = ICBMConstants.TICKS_MIN * 2;
    protected ThreadSmallExplosion thread;
    protected Set<EntityFlyingBlock> flyingBlocks = new HashSet<EntityFlyingBlock>();
    List<BlockPos> results;

    int searchIndex = 0;

    @Override
    public boolean setupBlast() {
        if (!this.world().isRemote) {
            this.thread = new ThreadSmallExplosion(this, (int) this.getBlastRadius(), this.exploder);
            this.thread.start();
        }

        //this.oldWorld().playSoundEffect(position.x(), position.y(), position.z(), References.PREFIX + "antigravity", 6.0F, (1.0F + (oldWorld().rand.nextFloat() - oldWorld().rand.nextFloat()) * 0.2F) * 0.7F);
        return true;
    }

    @Override
    public boolean doRun(int loops, Consumer<BlockPos> edits) {
        BlastHelpers.forEachPosInRadius(this.getBlastRadius(), (x, y, z) -> {
            edits.accept(new BlockPos(xi() + x, yi() + y, zi() + z));
        });
        return false;
    }

    @Override
    public boolean doExplode(int callCount) //TODO rewrite entire method
    {
        if (world() != null && !this.world().isRemote) {
            if (this.thread != null) //TODO replace thread check with callback triggered by thread and delayed into main thread
            {
                if (this.thread.isComplete) {
                    /// Replace thread with real-time y-layer search that uses some bitmask to detect possible targets
                    if (results == null) {
                        results = new ArrayList(getThreadResults());
                        Collections.shuffle(results);
                        results.sort(Comparator.comparingInt(r -> -r.getY()));
                        this.threadResults.clear();
                    }

                    long startTime = System.nanoTime();

                    if(searchIndex >= results.size()) {
                        searchIndex = 0;
                    }

                    // Search until we find a single block
                    for (; searchIndex < results.size(); searchIndex++) {
                        final BlockPos targetPosition = results.get(searchIndex); //TODO calculate position instead of pulling from thread

                        if (FlyingBlock.spawnFlyingBlock(world, targetPosition, (entity) -> {
                            entity.yawChange = 50 * world().rand.nextFloat();
                            entity.pitchChange = 100 * world().rand.nextFloat();
                            entity.motionY += Math.max(1 * world().rand.nextFloat(), 1);

                            double deltaX = targetPosition.getX() - this.location.getX();
                            double deltaZ = targetPosition.getZ() - this.location.getZ();
                            double mag = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

                            deltaX /= mag;
                            deltaZ /= mag;

                            entity.motionX += deltaX * (1 - world().rand.nextFloat());
                            entity.motionZ += deltaZ * (1 - world().rand.nextFloat());


                            entity.setGravity(0);
                            entity.setInAirKillTime(RUNNING_TICKS * 2);
                        }, entityFlyingBlock -> {
                            flyingBlocks.add(entityFlyingBlock);
                            ICBMClassic.logger().info("Spawned flying block {}", entityFlyingBlock);
                        })) {
                           break;
                        }
                    }

                    long endTime = System.nanoTime() - startTime;

                    ICBMClassic.logger().info("{}({}, {}) - {}", this, callCount, this.results.size(), StringHelpers.formatNanoTime(endTime));
                }
            } else {
                String msg = String.format("BlastAntiGravitational#doPostExplode() -> Failed to run due to null thread" +
                        "\nWorld = %s " +
                        "\nThread = %s" +
                        "\nSize = %s" +
                        "\nPos = ",
                    world, thread, size, location);
                ICBMClassic.logger().error(msg);
            }
        }

        int radius = (int) this.getBlastRadius();
        final int affectHeight = Math.max(radius, 100); //TODO config affect height
        AxisAlignedBB bounds = new AxisAlignedBB(location.x() - radius, location.y() - radius, location.z() - radius, location.y() + radius, location.y() + affectHeight, location.z() + radius);
        List<Entity> allEntities = world().getEntitiesWithinAABB(Entity.class, bounds);

        for (Entity entity : allEntities) {
            if (entity.posY < affectHeight + location.y()) {
                if (entity.motionY < 0.4) {
                    entity.motionY += 0.15;
                }
            }
        }

        return this.callCount > RUNNING_TICKS;
    }

    @Override
    protected void onBlastCompleted() {
        flyingBlocks.forEach(EntityFlyingBlock::restoreGravity);
    }
}
