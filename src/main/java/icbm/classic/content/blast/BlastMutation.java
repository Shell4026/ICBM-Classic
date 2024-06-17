package icbm.classic.content.blast;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class BlastMutation extends Blast
{
    @Override
    public boolean doExplode(int callCount)
    {
        if (!this.world().isRemote)
        {
            final AxisAlignedBB bounds = new AxisAlignedBB(location.x() - this.getBlastRadius(), location.y() - this.getBlastRadius(), location.z() - this.getBlastRadius(), location.x() + this.getBlastRadius(), location.y() + this.getBlastRadius(), location.z() + this.getBlastRadius());
            final List<MobEntity> entitiesNearby = world().getEntitiesWithinAABB(MobEntity.class, bounds);

            for (MobEntity entity : entitiesNearby)
            {
                applyMutationEffect(entity);
            }
        }
        return false;
    }

    public static boolean applyMutationEffect(final LivingEntity entity)
    {
        if (entity instanceof PigEntity)
        {
            final ZombiePigmanEntity newEntity = new ZombiePigmanEntity(entity.world);
            newEntity.preventEntitySpawning = true;
            newEntity.setPosition(entity.posX, entity.posY, entity.posZ);
            entity.setDead();
            entity.world.spawnEntity(newEntity);
            return true;
        }
        else if (entity instanceof VillagerEntity)
        {
            final ZombieVillagerEntity newEntity = new ZombieVillagerEntity(entity.world);
            newEntity.preventEntitySpawning = true;
            newEntity.setPosition(entity.posX, entity.posY, entity.posZ);
            newEntity.setForgeProfession(((VillagerEntity) entity).getProfessionForge());
            entity.setDead();
            entity.world.spawnEntity(newEntity);
            return true;
        }
        return false;
    }

    @Override //disable the sound for this explosive
    protected void playExplodeSound()
    {
    }
}
