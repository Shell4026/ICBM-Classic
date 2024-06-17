package icbm.classic.content.items.behavior;

import icbm.classic.content.entity.EntityBombCart;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BombCartDispenseBehavior extends DefaultDispenseItemBehavior
{
    private final DefaultDispenseItemBehavior behaviourDefaultDispenseItem = new DefaultDispenseItemBehavior();

    @Override
    public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
    {
        Direction enumfacing = source.getBlockState().getValue(DispenserBlock.FACING);
        World world = source.getWorld();
        double x = source.getX() + (double) enumfacing.getFrontOffsetX() * 1.125D;
        double y = Math.floor(source.getY()) + (double) enumfacing.getFrontOffsetY();
        double z = source.getZ() + (double) enumfacing.getFrontOffsetZ() * 1.125D;
        BlockPos blockpos = source.getBlockPos().offset(enumfacing);
        BlockState iblockstate = world.getBlockState(blockpos);
        AbstractRailBlock.EnumRailDirection rail =
                (iblockstate.getBlock() instanceof AbstractRailBlock
                        ? ((AbstractRailBlock) iblockstate.getBlock()).getRailDirection(world, blockpos, iblockstate, null)
                                : AbstractRailBlock.EnumRailDirection.NORTH_SOUTH);

        double heightDelta;

        if (AbstractRailBlock.isRailBlock(iblockstate))
        {
            if (rail.isAscending())
            {
                heightDelta = 0.6D;
            }
            else
            {
                heightDelta = 0.1D;
            }
        }
        else
        {
            if (iblockstate.getMaterial() != Material.AIR || !AbstractRailBlock.isRailBlock(world.getBlockState(blockpos.down())))
            {
                return this.behaviourDefaultDispenseItem.dispense(source, stack);
            }

            BlockState blockB = world.getBlockState(blockpos.down());
            AbstractRailBlock.EnumRailDirection railB =
                    (blockB.getBlock() instanceof AbstractRailBlock ?
                            ((AbstractRailBlock) blockB.getBlock()).getRailDirection(world, blockpos.down(), blockB, null)
                            : AbstractRailBlock.EnumRailDirection.NORTH_SOUTH);

            if (enumfacing != Direction.DOWN && railB.isAscending())
            {
                heightDelta = -0.4D;
            }
            else
            {
                heightDelta = -0.9D;
            }
        }

        EntityBombCart cart = new EntityBombCart(world, x, y + heightDelta, z, stack);

        if (stack.hasDisplayName())
        {
            cart.setCustomNameTag(stack.getDisplayName());
        }

        world.spawnEntity(cart);
        stack.shrink(1);
        return stack;
    }
}
