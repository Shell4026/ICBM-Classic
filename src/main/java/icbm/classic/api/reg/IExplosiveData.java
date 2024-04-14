package icbm.classic.api.reg;

import icbm.classic.api.EnumTier;
import icbm.classic.api.actions.IActionData;
import icbm.classic.api.actions.data.IActionFieldProvider;
import icbm.classic.api.actions.cause.IActionSource;
import icbm.classic.api.explosion.IBlastInit;
import icbm.classic.api.reg.content.IExplosiveContentRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Stores data about an explosive
 * <p>
 * <p>
 * Created by Dark(DarkGuardsman, Robin) on 1/4/19.
 *
 * @deprecated will be replaced by {@link IActionData}
 */
public interface IExplosiveData extends Comparable<IExplosiveData>, IActionData
{
    /**
     * Assigned ID of the explosive. Is
     * saved to config file and automatically
     * assigned for new explosives.
     *
     * @return ID
     * @deprecated will be removed in MC 1.13 and replaced with {@link #getRegistryKey()}
     */
    int getRegistryID();

    /**
     * Tier of the explosive.
     *
     * @return tier of the explosive
     */
    @Deprecated
    @Nonnull
    default EnumTier getTier() {
        // Only for legacy explosives, moving forward tier is visual only and has no functionality
        return EnumTier.NONE;
    }
}
