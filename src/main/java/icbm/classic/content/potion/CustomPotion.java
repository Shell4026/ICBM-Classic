package icbm.classic.content.potion;

import icbm.classic.ICBMConstants;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;

public abstract class CustomPotion extends Effect
{
    /**
     * Creates a new type of potion
     *
     * @param isBadEffect - Is this potion a good potion or a bad one?
     * @param color       - The color of this potion.
     * @param name        - The name of this potion.
     */
    public CustomPotion(boolean isBadEffect, int color, int id, String name)
    {
        super(isBadEffect, color);
        this.setPotionName("potion." + name);
        REGISTRY.register(id, new ResourceLocation(ICBMConstants.PREFIX + name), this);
    }

    @Override
    public Effect setIconIndex(int par1, int par2)
    {
        super.setIconIndex(par1, par2);
        return this;
    }

    @Override
    protected Effect setEffectiveness(double par1)
    {
        super.setEffectiveness(par1);
        return this;
    }
}
