package icbm.classic.mods.jei;

import lombok.AllArgsConstructor;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class CargoItemWrapper implements ICraftingRecipeWrapper {

    private final ItemStack output;
    private final List<ItemStack> options;
    private final ResourceLocation regName;

    @Override
    public void getIngredients(IIngredients iIngredients) {

        iIngredients.setInputLists(ItemStack.class, Arrays.asList(Collections.singletonList(output), options));
        iIngredients.setOutput(ItemStack.class, output);
    }

    @Override
    @Nullable
    public ResourceLocation getRegistryName() {
        return regName;
    }
}
