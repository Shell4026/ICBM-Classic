package icbm.classic.client;

import icbm.classic.content.reg.BlockReg;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;

/**
 *
 * Created by Dark(DarkGuardsman, Robin) on 6/19/2017.
 */
public class SpikeMeshDefinition implements ItemMeshDefinition
{
    public final net.minecraft.client.renderer.model.ModelResourceLocation base;
    public final ModelResourceLocation fire;
    public final ModelResourceLocation poison;

    public static SpikeMeshDefinition INSTANCE;

    private SpikeMeshDefinition()
    {
        base = new net.minecraft.client.renderer.model.ModelResourceLocation(BlockReg.blockSpikes.getRegistryName(), "inventory");
        fire = new ModelResourceLocation(BlockReg.blockSpikes.getRegistryName() + "_fire", "inventory");
        poison = new ModelResourceLocation(BlockReg.blockSpikes.getRegistryName() + "_poison", "inventory");

        net.minecraft.client.renderer.model.ModelBakery.registerItemVariants(Item.getItemFromBlock(BlockReg.blockSpikes), base);
        ModelBakery.registerItemVariants(Item.getItemFromBlock(BlockReg.blockSpikes), base);
        net.minecraft.client.renderer.model.ModelBakery.registerItemVariants(Item.getItemFromBlock(BlockReg.blockSpikes), fire);
        net.minecraft.client.renderer.model.ModelBakery.registerItemVariants(Item.getItemFromBlock(BlockReg.blockSpikes), poison);
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(BlockReg.blockSpikes), this);
    }

    public static void init()
    {
        INSTANCE = new SpikeMeshDefinition();
    }

    @Override
    public net.minecraft.client.renderer.model.ModelResourceLocation getModelLocation(ItemStack stack)
    {
        if (stack.getItemDamage() == 1)
        {
            return poison;
        }
        else if (stack.getItemDamage() == 2)
        {
            return fire;
        }
        return base;
    }
}
