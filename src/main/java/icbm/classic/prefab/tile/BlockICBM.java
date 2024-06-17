package icbm.classic.prefab.tile;

import icbm.classic.ICBMClassic;
import icbm.classic.ICBMConstants;
import icbm.classic.lib.InventoryUtility;
import net.minecraft.block.Block;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockICBM extends Block
{
    public static final PropertyDirection ROTATION_PROP = PropertyDirection.create("rotation");

    protected boolean dropInventory = false;

    public BlockICBM(String name, Material mat)
    {
        super(mat);
        blockHardness = 10f;
        blockResistance = 10f;
        setRegistryName(ICBMConstants.DOMAIN, name.toLowerCase());
        setUnlocalizedName(ICBMConstants.PREFIX + name.toLowerCase());
        setCreativeTab(ICBMClassic.CREATIVE_TAB);
    }

    public BlockICBM(String name)
    {
        this(name, Material.IRON);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        super.stateContainer
        return new BlockStateContainer(this, ROTATION_PROP);
    }

    @Override
    public BlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(ROTATION_PROP, Direction.getFront(meta));
    }

    @Override
    public int getMetaFromState(BlockState state)
    {
        return state.getValue(ROTATION_PROP).ordinal();
    }

    @Override
    public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer, Hand hand)
    {
        return getDefaultState().withProperty(ROTATION_PROP, placer.getHorizontalFacing());
    }

    @Override
    public void breakBlock(World world, BlockPos pos, BlockState state)
    {
        if(dropInventory) {
            InventoryUtility.dropInventory(world, pos);
        }
        super.breakBlock(world, pos, state);
    }
}
