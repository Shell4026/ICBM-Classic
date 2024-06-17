package icbm.classic.content.blocks;

import icbm.classic.ICBMConstants;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGlassPressurePlate extends PressurePlateBlock
{
    public BlockGlassPressurePlate()
    {
        super(Material.GLASS, Sensitivity.EVERYTHING);
        this.setTickRandomly(true);
        this.setResistance(1F);
        this.setHardness(0.3F);
        this.setSoundType(SoundType.GLASS);
        this.setRegistryName(ICBMConstants.PREFIX + "glassPressurePlate");
        this.setUnlocalizedName(ICBMConstants.PREFIX + "glassPressurePlate");
        this.setCreativeTab(ItemGroup.REDSTONE);
        this.setDefaultState(getDefaultState().withProperty(POWERED, false));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(BlockState state)
    {
        return false;
    }
}
