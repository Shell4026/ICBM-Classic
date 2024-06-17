package icbm.classic.content.blocks.emptower;

import icbm.classic.ICBMClassic;
import icbm.classic.ICBMConstants;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
public class BlockEmpTower extends ContainerBlock
{
    public static final PropertyTowerStates TOWER_MODELS = new PropertyTowerStates();
    public static BlockState COIL;
    public static BlockState ELECTRIC;

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
    public int damageDropped(BlockState state)
    {
        return getMetaFromState(state);
    }

    @Override
    public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer, Hand hand)
    {
        return getStateFromMeta(meta);
    }

    @Override
    public BlockState getStateFromMeta(int meta)
    {
        if(meta == 1) {
            return getDefaultState().withProperty(TOWER_MODELS, PropertyTowerStates.EnumTowerTypes.SPIN);
        }
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(BlockState state)
    {
        if(state.getValue(TOWER_MODELS) == PropertyTowerStates.EnumTowerTypes.SPIN) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            playerIn.openGui(ICBMClassic.INSTANCE, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public boolean isOpaqueCube(BlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(BlockState state)
    {
        return false;
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state)
    {
        return false;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos)
    {
        return 0; //TODO output charge amount
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer)
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
    public void getSubBlocks(ItemGroup tab, NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
    }
}
