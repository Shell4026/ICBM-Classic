package icbm.classic.content.blast;

import icbm.classic.content.entity.EntityFragments;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlastShrapnel extends Blast
{
    @Setter @Accessors(chain = true)
    private Function<World, EntityFragments> projectile;

    @Override
    public boolean doExplode(int callCount)
    {
        if (!world().isRemote)
        {
            float amountToRotate = 360 / this.getBlastRadius();

            for (int i = 0; i < this.getBlastRadius(); i++)
            {
                // Try to do a 360 explosion on all 6 faces of the cube.
                float rotationYaw = 0.0F + amountToRotate * i;

                for (int ii = 0; ii < this.getBlastRadius(); ii++)
                {
                    final EntityFragments arrow = projectile.apply(world);
                    arrow.setPosition(location.x(), location.y(), location.z());

                    float rotationPitch = 0.0F + amountToRotate * ii;
                    arrow.setLocationAndAngles(location.x(), Math.floor(location.y()) + 1.5, location.z(), rotationYaw, rotationPitch);
                    arrow.posX -= (MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
                    arrow.posY -= 0.10000000149011612D;
                    arrow.posZ -= (MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
                    arrow.setPosition(arrow.posX, arrow.posY, arrow.posZ);

                    arrow.motionX = (-MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI));
                    arrow.motionZ = (MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI));
                    arrow.motionY = (-MathHelper.sin(rotationPitch / 180.0F * (float) Math.PI));

                    arrow.setArrowHeading(arrow.motionX * world().rand.nextFloat(), arrow.motionY * world().rand.nextFloat(), arrow.motionZ * world().rand.nextFloat(), 0.5f + (0.7f * world().rand.nextFloat()), 1.0F);
                    world().spawnEntity(arrow);

                }
            }
        }
        return false;
    }
}
