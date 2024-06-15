package icbm.classic.api.missiles;

import net.minecraft.item.ItemStack;

/**
 * Version of capability to apply specific to ItemStacks for additional features
 * <p>
 * Items can either be the missile directly or a container of missile(s)
 *
 * @deprecated will be replaced with {@link icbm.classic.api.actions.IActionData}
 */
@Deprecated
public interface ICapabilityMissileStack extends ICapabilityMissileBuilder
{

    /**
     * Called when a missile is built and
     * should be consumed from the builder.
     *
     * @return stack to return to inventory
     */
    default ItemStack consumeMissile()
    {
        return ItemStack.EMPTY;
    }
}
