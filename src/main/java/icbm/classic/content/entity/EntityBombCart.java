package icbm.classic.content.entity;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.refs.ICBMExplosives;
import icbm.classic.content.blocks.explosive.BlockExplosive;
import icbm.classic.content.missile.logic.source.ActionSource;
import icbm.classic.content.missile.logic.source.cause.EntityCause;
import icbm.classic.content.reg.BlockReg;
import icbm.classic.content.reg.ItemReg;
import icbm.classic.lib.NBTConstants;
import icbm.classic.lib.capability.ex.CapabilityExplosiveEntity;
import icbm.classic.prefab.tile.BlockICBM;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import java.util.Optional;

public class EntityBombCart extends EntityMinecartTNT implements IEntityAdditionalSpawnData
{
    public final CapabilityExplosiveEntity explosive = new CapabilityExplosiveEntity(this);

    public EntityBombCart(World par1World)
    {
        super(par1World);
    }

    public EntityBombCart(World par1World, double x, double y, double z, ItemStack itemStack) //TODO change to pass in itemstack for capability
    {
        super(par1World, x, y, z);
        this.explosive.setStack(itemStack);
    }

    @Override
    public void writeSpawnData(ByteBuf data)
    {
        ByteBufUtils.writeItemStack(data, explosive.toStack());
    }

    @Override
    public void readSpawnData(ByteBuf data)
    {
        explosive.setStack(ByteBufUtils.readItemStack(data));
    }

    @Override
    protected void explodeCart(double par1)
    {
        explosive.doExplosion(this.posX, this.posY, this.posZ, new ActionSource(world, new Vec3d(posX, posY, posZ), new EntityCause(this))); //TODO handle output and include trigger source & player of the cart
        this.setDead();
    }

    @Override
    public void killMinecart(DamageSource par1DamageSource)
    {
        if (!world.isRemote)
        {
            this.setDead();
            double d0 = this.motionX * this.motionX + this.motionZ * this.motionZ;

            if (!par1DamageSource.isExplosion())
            {
                this.entityDropItem(getCartItem(), 0.0F);
            }

            if (par1DamageSource.isFireDamage() || par1DamageSource.isExplosion() || d0 >= 0.009999999776482582D)
            {
                this.explodeCart(d0);
            }
        }
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (isIgnited())
        {
            ICBMClassicAPI.EX_MINECART_REGISTRY.tickFuse(this, explosive.getExplosiveData(), minecartTNTFuse);
        }
    }

    @Override
    public void ignite()
    {
        this.minecartTNTFuse = ICBMClassicAPI.EX_MINECART_REGISTRY.getFuseTime(this, explosive.getExplosiveData());

        if (!this.world.isRemote)
        {
            this.world.setEntityState(this, (byte) 10);

            if (!this.isSilent())
            {
                this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public EntityItem entityDropItem(ItemStack stack, float offsetY)
    {
        if (stack.getItem() == Item.getItemFromBlock(Blocks.TNT))
        {
            return super.entityDropItem(getCartItem(), offsetY);
        }
        return super.entityDropItem(stack, offsetY);
    }

    @Override
    public ItemStack getCartItem()
    {
        return explosive.toStack();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setTag("explosive", explosive.serializeNBT());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);

        //Legacy
        if(nbt.hasKey(NBTConstants.EXPLOSIVE, 99)) {
            explosive.setStack(new ItemStack(ItemReg.itemBombCart, 1, nbt.getInteger(NBTConstants.EXPLOSIVE)));
        }
        else {
            explosive.deserializeNBT(nbt.getCompoundTag("explosive"));
        }
    }

    @Override
    public IBlockState getDefaultDisplayTile()
    {
        return BlockReg.blockExplosive.getDefaultState()
                .withProperty(BlockExplosive.EX_PROP, Optional.ofNullable(explosive.getExplosiveData()).orElse(ICBMExplosives.CONDENSED))
                .withProperty(BlockICBM.ROTATION_PROP, EnumFacing.UP);
    }
}
