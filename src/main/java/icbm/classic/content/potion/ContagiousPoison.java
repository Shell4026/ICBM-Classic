package icbm.classic.content.potion;

import icbm.classic.lib.transform.vector.Pos;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;

/**
 * @deprecated remove at some point and apply potion effects directly
 */
@Deprecated
public class ContagiousPoison extends Poison
{
    private final boolean isContagious;

    public ContagiousPoison(String name, int id, boolean isContagious)
    {
        super(name);
        this.isContagious = isContagious;
    }

    @Override
    protected void doPoisonEntity(Pos emitPosition, EntityLivingBase entity, int amplifier)
    {
        if (this.isContagious)
        {
            entity.addPotionEffect(new CustomPotionEffect(MobEffects.POISON, 45 * 20, amplifier, null)); //TODO was contagious potion
            entity.addPotionEffect(new CustomPotionEffect(MobEffects.BLINDNESS, 15 * 20, amplifier));
        }
        else
        {
            entity.addPotionEffect(new CustomPotionEffect(MobEffects.POISON, 30 * 20, amplifier, null)); //TODO was toxin potion
            entity.addPotionEffect(new CustomPotionEffect(MobEffects.NAUSEA, 30 * 20, amplifier));
        }

        entity.addPotionEffect(new CustomPotionEffect(MobEffects.HUNGER, 30 * 20, amplifier));
        entity.addPotionEffect(new CustomPotionEffect(MobEffects.WEAKNESS, 35 * 20, amplifier));
        entity.addPotionEffect(new CustomPotionEffect(MobEffects.MINING_FATIGUE, 60 * 20, amplifier));
    }
}
