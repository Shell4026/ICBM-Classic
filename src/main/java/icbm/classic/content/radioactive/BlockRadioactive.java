package icbm.classic.content.radioactive;

import com.google.common.collect.Lists;
import icbm.classic.ICBMClassic;
import icbm.classic.ICBMConstants;
import icbm.classic.config.ConfigMain;
import icbm.classic.content.gas.ProtectiveArmorHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockRadioactive extends Block {
    public static final PropertyType TYPE_PROP = new PropertyType();

    public static final DamageSource damageSource = new DamageSource("icbmclassic:radioactive_block");

    public float decayChance = 0.85f; //TODO configs
    public int areaOfEffect = 5;
    public int tickRate = 5;
    public float damage = 2;

    public BlockRadioactive() {
        super(Material.ROCK);
        this.setDefaultState(getDefaultState().withProperty(TYPE_PROP, EnumType.STONE));
        this.setRegistryName(ICBMConstants.PREFIX + "radioactive");
        this.setUnlocalizedName(ICBMConstants.PREFIX + "radioactive");
        this.setCreativeTab(ICBMClassic.CREATIVE_TAB);
        this.setHardness(0.5f);
        this.setTickRandomly(true);
    }

    @Override
    public int tickRate(World worldIn)
    {
        return tickRate;
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, BlockState state, Random random) {
        if (!worldIn.isRemote)
        {
            final AxisAlignedBB bounds = new AxisAlignedBB(pos).grow(areaOfEffect, areaOfEffect, areaOfEffect);
            final List<LivingEntity> entities = worldIn.getEntitiesWithinAABB(LivingEntity.class, bounds);
            for(LivingEntity entity : entities) {
                if(random.nextFloat() < decayChance) {
                    float protection = ProtectiveArmorHandler.getProtectionRating(entity);
                    if (protection < ConfigMain.protectiveArmor.minProtectionRadiation || protection < random.nextFloat()) {
                        entity.attackEntityFrom(damageSource, damage); //TODO consider reducing damage by random amount of protection found. This way iron armor can reduce damage every so often
                        entity.addPotionEffect(new EffectInstance(Effects.WITHER, 20));
                    }
                }
            }

            worldIn.scheduleUpdate(pos, this, Math.max(1, random.nextInt(tickRate)));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(12) == 0)
        {
            worldIn.playSound(((float)pos.getX() + 0.5F), ((float)pos.getY() + 0.5F), ((float)pos.getZ() + 0.5F),
                SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, //TODO get custom audio
                1.0F + rand.nextFloat(), rand.nextFloat() * 1.7F + 0.3F, false);
        }

        //TODO spawn particles showing AOE
    }

    @Deprecated
    public float getBlockHardness(BlockState blockState, World worldIn, BlockPos pos) {
        if (blockState.getProperties().containsKey(TYPE_PROP)) {
            final EnumType type = (EnumType) blockState.getProperties().get(TYPE_PROP);
            switch (type) {
                case DIRT:
                    return Blocks.DIRT.getBlockHardness(net.minecraft.block.Blocks.DIRT.getDefaultState(), worldIn, pos);
                case STONE:
                    return net.minecraft.block.Blocks.STONE.getBlockHardness(net.minecraft.block.Blocks.STONE.getDefaultState(), worldIn, pos);

            }
        }
        return this.blockHardness;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        final BlockState blockState = world.getBlockState(pos);
        if (blockState.getProperties().containsKey(TYPE_PROP)) {
            final EnumType type = (EnumType) blockState.getProperties().get(TYPE_PROP);
            switch (type) {
                case DIRT:
                    return net.minecraft.block.Blocks.DIRT.getExplosionResistance(world, pos, exploder, explosion);
                case STONE:
                    return net.minecraft.block.Blocks.STONE.getExplosionResistance(world, pos, exploder, explosion);
            }
        }
        return getExplosionResistance(exploder);
    }

    @Override
    public int damageDropped(BlockState state) {
        return getMetaFromState(state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE_PROP);
    }

    @Override
    public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer, Hand hand) {
        return getDefaultState().withProperty(TYPE_PROP, EnumType.get(meta));
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(TYPE_PROP).ordinal();
    }

    @Deprecated
    public BlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE_PROP, EnumType.get(meta));
    }

    @Override
    public void getSubBlocks(ItemGroup tab, NonNullList<ItemStack> items) {
        if (tab == this.getCreativeTabToDisplayOn()) {
            for (EnumType type : EnumType.values()) {
                items.add(new ItemStack(this, 1, type.ordinal()));
            }
        }
    }

    public static class PropertyType extends PropertyEnum<EnumType> {
        public PropertyType() {
            super("type", EnumType.class, Lists.newArrayList(EnumType.values()));
        }
    }

    public static enum EnumType implements IStringSerializable {
        DIRT,
        STONE;

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public static EnumType get(int meta) {
            return meta >= 0 && meta < values().length ? values()[meta] : STONE;
        }
    }
}