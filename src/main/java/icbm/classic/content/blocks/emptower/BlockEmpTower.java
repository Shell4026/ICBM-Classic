package icbm.classic.content.blocks.emptower;

import icbm.classic.ICBMClassic;
import icbm.classic.ICBMConstants;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 *
 * Created by Dark(DarkGuardsman, Robin) on 1/23/2018.
 */
public class BlockEmpTower extends BlockContainer
{
    public static final PropertyTowerStates TOWER_MODELS = new PropertyTowerStates();
    public static IBlockState COIL;
    public static IBlockState ELECTRIC;

    public BlockEmpTower()
    {
        super(Material.IRON);
        blockHardness = 10f;
        blockResistance = 10f;
        setRegistryName(ICBMConstants.DOMAIN, "emptower");
        setUnlocalizedName(ICBMConstants.PREFIX + "emptower");
        setCreativeTab(ICBMClassic.CREATIVE_TAB);

        COIL = getDefaultState().withProperty(TOWER_MODELS, PropertyTowerStates.EnumTowerTypes.COIL);
        ELECTRIC = getDefaultState().withProperty(TOWER_MODELS, PropertyTowerStates.EnumTowerTypes.ELECTRIC);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, TOWER_MODELS) {};
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return getMetaFromState(state);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return getStateFromMeta(meta);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        if(meta == 1) {
            return getDefaultState().withProperty(TOWER_MODELS, PropertyTowerStates.EnumTowerTypes.SPIN);
        }
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        if(state.getValue(TOWER_MODELS) == PropertyTowerStates.EnumTowerTypes.SPIN) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            playerIn.openGui(ICBMClassic.INSTANCE, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return false;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return 0; //TODO output charge amount
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        if(state == ELECTRIC) {
            return BlockRenderLayer.TRANSLUCENT == layer;
        }
        return BlockRenderLayer.SOLID == layer;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        if(meta == 1) {
            return new TileEmpTowerFake();
        }
        return new TileEMPTower();
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
    }
}
