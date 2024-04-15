package icbm.classic.content.cluster.missile;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.missiles.projectile.IProjectileStack;
import icbm.classic.content.cargo.CargoProjectileData;
import icbm.classic.lib.projectile.ProjectileStack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Supplier;


/**
 * Recipe for adding cargo to cargo projectile item
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class RecipeCluster extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    private final ItemStack recipeOutput;
    private final Supplier<CargoProjectileData> dataBuilder;

    @Override
    public boolean isDynamic()
    {
        return true;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        // Don't leave container items, since we store the entire item
        return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ItemStack cluster = ItemStack.EMPTY;
        int itemCount = 0;

        for(int slot = 0; slot < inv.getSizeInventory(); slot++) {
            final ItemStack slotStack = inv.getStackInSlot(slot);
            if(slotStack.isItemEqual(recipeOutput)) {
                if(!cluster.isEmpty()) {
                    return false; // Can't nest clusters
                }
                cluster = slotStack;
            }
            else if(!slotStack.isEmpty()) {
                itemCount++;
            }
        }

        //TODO get currently stored items in cluster
        //TODO count value size of stored
        //TODO count value size of new
        //TODO if value count + new > config max... refuse to craft


        return itemCount > 1 && !cluster.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        final ItemStack output = null;

        // TODO get current cluster item
        // TODO add items

        return output;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }
}
