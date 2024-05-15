package icbm.classic.content.radioactive;

import icbm.classic.config.blast.ConfigBlast;
import icbm.classic.config.util.BlockStateConfigList;
import icbm.classic.content.reg.BlockReg;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class RadioactiveHandler {
    public static final BlockStateConfigList.BlockOut radioactiveBlockSwaps = new BlockStateConfigList.BlockOut("[RadioactiveReplacements]", (configList) -> {
        configList.setDefault(Blocks.STONE.getRegistryName(), BlockReg.blockRadioactive.getDefaultState().withProperty(BlockRadioactive.TYPE_PROP, BlockRadioactive.EnumType.STONE), 0);

        configList.setDefault(Blocks.DIRT.getRegistryName(), BlockReg.blockRadioactive.getDefaultState().withProperty(BlockRadioactive.TYPE_PROP, BlockRadioactive.EnumType.DIRT), 0);
        configList.setDefault(Blocks.FARMLAND.getRegistryName(), BlockReg.blockRadioactive.getDefaultState().withProperty(BlockRadioactive.TYPE_PROP, BlockRadioactive.EnumType.DIRT), 0);
        configList.setDefault(Blocks.GRASS_PATH.getRegistryName(), BlockReg.blockRadioactive.getDefaultState().withProperty(BlockRadioactive.TYPE_PROP, BlockRadioactive.EnumType.DIRT), 0);
        configList.setDefault(Blocks.GRASS.getRegistryName(), BlockReg.blockRadioactive.getDefaultState().withProperty(BlockRadioactive.TYPE_PROP, BlockRadioactive.EnumType.DIRT), 0);
        configList.setDefault(Blocks.MYCELIUM.getRegistryName(), BlockReg.blockRadioactive.getDefaultState().withProperty(BlockRadioactive.TYPE_PROP, BlockRadioactive.EnumType.DIRT), 0);

        for(Block block: ForgeRegistries.BLOCKS) {
            final Material material = block.getMaterial(block.getDefaultState());
            if(material == Material.PLANTS || material == Material.LEAVES || material == Material.VINE) {
                configList.setDefault(block.getRegistryName(), Blocks.AIR.getDefaultState(), 0);
            }
        }

        configList.load(ConfigBlast.nuclear.radiationReplacements.blockStates);
    });

    public static void loadFromConfig() {
        radioactiveBlockSwaps.reload();
    }

    public static void setup() {
        loadFromConfig();
    }
}
