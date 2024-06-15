package icbm.classic.api;

import icbm.classic.lib.LanguageUtility;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextFormatting;

/**
 *
 * Created by Dark(DarkGuardsman, Robin) on 1/31/2018.
 */
public enum EnumTier implements IStringSerializable
{
    ONE(TextFormatting.GREEN),
    TWO(TextFormatting.YELLOW),
    THREE(TextFormatting.GOLD),
    FOUR(TextFormatting.RED),
    NONE(TextFormatting.WHITE); //TODO we should move the colors to the translations directly

    private TextFormatting tooltipColor;

    EnumTier(TextFormatting tooltipColor)
    {
        this.tooltipColor = tooltipColor;
    }

    public TextFormatting getTooltipColor()
    {
        return tooltipColor;
    }

    @Override
    public String toString()
    {
        return this.getName();
    }

    public String getLocalizedName()
    {
        return LanguageUtility.getLocal("tier.icbmclassic." + getName());
    }

    @Override
    public String getName()
    {
        return name().toLowerCase();
    }

    public static EnumTier get(int itemDamage)
    {
        if (itemDamage > 0 && itemDamage < values().length)
        {
            return values()[itemDamage];
        }
        return ONE;
    }
}
