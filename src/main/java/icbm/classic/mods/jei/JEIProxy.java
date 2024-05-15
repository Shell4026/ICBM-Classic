package icbm.classic.mods.jei;

import icbm.classic.ICBMConstants;
import icbm.classic.content.reg.ItemReg;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JEIPlugin
public class JEIProxy implements IModPlugin {
    // https://github.com/mezz/JustEnoughItems/releases/tag/v4.7.11
    // https://github.com/mezz/JustEnoughItems/tree/8a8c25448c126decefe8f12f65fc88cae94d4012
    // https://github.com/mekanism/Mekanism/tree/1.12/src/main/java/mekanism/client/jei
    // https://github.com/mezz/JustEnoughItems/wiki/Getting-Started

    @Override
    public void register(IModRegistry registry) {
        //https://github.com/ExtraCells/ExtraCells2/blob/b1d03907d911909aaa3b176a7cc90eac0c1467dc/src/main/scala/extracells/integration/jei/Plugin.scala

        final List recipes = new ArrayList();
        recipes.add(new CargoItemWrapper(new ItemStack(ItemReg.itemBalloon), registry.getIngredientRegistry().getIngredients(ItemStack.class), new ResourceLocation(ICBMConstants.DOMAIN, "balloon_cargo")));
        recipes.add(new CargoItemWrapper(new ItemStack(ItemReg.itemParachute), registry.getIngredientRegistry().getIngredients(ItemStack.class), new ResourceLocation(ICBMConstants.DOMAIN, "parachute_cargo")));
        //TODO cluster
        registry.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);
    }
}
