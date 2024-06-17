package icbm.classic.content.potion;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

import java.util.ArrayList;
import java.util.List;

public class CustomPotionEffect extends EffectInstance
{

    public CustomPotionEffect(Effect potion, int duration, int amplifier)
    {
        super(potion, duration, amplifier);
    }

    /** Creates a potion effect with custom curable items.
     *
     * @param curativeItems - ItemStacks that can cure this potion effect */
    public CustomPotionEffect(Effect potionID, int duration, int amplifier, List<ItemStack> curativeItems)
    {
        super(potionID, duration, amplifier);

        if (curativeItems == null)
        {
            this.setCurativeItems(new ArrayList<ItemStack>());
        }
        else
        {
            this.setCurativeItems(curativeItems);
        }
    }
}
