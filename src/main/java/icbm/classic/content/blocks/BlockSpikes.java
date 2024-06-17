package icbm.classic.content.blocks;

import com.google.common.collect.Lists;
import icbm.classic.ICBMClassic;
import icbm.classic.ICBMConstants;
import icbm.classic.config.ConfigMain;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSpikes extends Block
{
    public static final SpikeProperty SPIKE_PROPERTY = new SpikeProperty();

    public static final DamageSource DAMAGE_SOURCE = new DamageSource("icbmclassic:spikes");

    public BlockSpikes()
    {
        super(Material.IRON);
        this.setRegistryName(ICBMConstants.PREFIX + "spikes");
        this.setUnlocalizedName(ICBMConstants.PREFIX + "spikes");
        this.setCreativeTab(ICBMClassic.CREATIVE_TAB);
        this.setHardness(1.0F);
    }

    @Override
    public int damageDropped(BlockState state)
    {
        return getMetaFromState(state);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return null;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, SPIKE_PROPERTY);
    }

    @Override
    public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer, Hand hand)
    {
        return getDefaultState().withProperty(SPIKE_PROPERTY, EnumSpikes.get(meta));
    }

    @Override
    public int getMetaFromState(BlockState state)
    {
        return state.getValue(SPIKE_PROPERTY).ordinal();
    }

    @Deprecated
    public BlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(SPIKE_PROPERTY, EnumSpikes.get(meta));
    }

    @Override
    public boolean isBlockNormalCube(BlockState blockState)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(BlockState blockState)
    {
        return false;
    }

    @Override
    public boolean isFullCube(BlockState state)
    {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, BlockState state, Entity entity)
    {
        // If the entity is a living entity
        if (entity instanceof LivingEntity)
        {
            if (world.getBlockState(pos).getValue(SPIKE_PROPERTY) == EnumSpikes.POISON) //TODO replace with state
            {
                entity.attackEntityFrom(DAMAGE_SOURCE, ConfigMain.spikes.poisonDamage);
                ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.POISON, 7 * 20, 0));
            }
            else if (world.getBlockState(pos).getValue(SPIKE_PROPERTY) == EnumSpikes.FIRE)
            {
                entity.attackEntityFrom(DAMAGE_SOURCE, ConfigMain.spikes.fireDamage);
                entity.setFire(7);
            }
            else {
                entity.attackEntityFrom(DAMAGE_SOURCE, ConfigMain.spikes.normalDamage);
            }
        }
    }

    @Override
    public void getSubBlocks(ItemGroup tab, NonNullList<ItemStack> items)
    {
        if (tab == this.getCreativeTabToDisplayOn())
        {
            for (EnumSpikes spikes : EnumSpikes.values())
            {
                items.add(new ItemStack(this, 1, spikes.ordinal()));
            }
        }
    }

    public static class SpikeProperty extends PropertyEnum<EnumSpikes>
    {
        protected SpikeProperty()
        {
            super("type", EnumSpikes.class, Lists.newArrayList(EnumSpikes.values()));
        }
    }

    public static enum EnumSpikes implements IStringSerializable
    {
        NORMAL,
        POISON,
        FIRE;

        @Override
        public String toString()
        {
            return this.getName();
        }

        @Override
        public String getName()
        {
            return name().toLowerCase();
        }

        public static EnumSpikes get(int meta)
        {
            return meta >= 0 && meta < values().length ? values()[meta] : NORMAL;
        }
    }
}
