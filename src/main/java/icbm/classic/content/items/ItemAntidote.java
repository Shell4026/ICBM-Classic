package icbm.classic.content.items;

import icbm.classic.prefab.item.ItemBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.UseAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemAntidote extends ItemBase
{

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entityLiving)
    {
        if (!world.isRemote)
        {
            entityLiving.clearActivePotions();
        }
        if (!(entityLiving instanceof PlayerEntity) || !((PlayerEntity) entityLiving).capabilities.isCreativeMode)
        {
            stack.shrink(1);
        }
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 32;
    }

    @Override
    public UseAction getItemUseAction(ItemStack par1ItemStack)
    {
        return UseAction.EAT;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        playerIn.setActiveHand(handIn);
        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemstack);
    }
}
