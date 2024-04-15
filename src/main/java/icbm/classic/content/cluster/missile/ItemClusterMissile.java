package icbm.classic.content.cluster.missile;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.ICBMClassicHelpers;
import icbm.classic.api.caps.IExplosive;
import icbm.classic.api.refs.ICBMExplosives;
import icbm.classic.api.reg.IExplosiveData;
import icbm.classic.content.cluster.bomblet.BombletProjectileData;
import icbm.classic.content.blocks.explosive.ItemBlockExplosive;
import icbm.classic.content.cargo.parachute.ParachuteProjectileData;
import icbm.classic.content.reg.BlockReg;
import icbm.classic.content.reg.ItemReg;
import icbm.classic.lib.capability.ex.CapabilityExplosiveStack;
import icbm.classic.lib.capability.missile.CapabilityMissileStack;
import icbm.classic.lib.projectile.vanilla.ArrowProjectileData;
import icbm.classic.prefab.item.ItemBase;
import icbm.classic.prefab.item.ItemStackCapProvider;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;
import java.util.List;

public class ItemClusterMissile extends ItemBase
{
    public ItemClusterMissile()
    {
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
    }

    @Override
    @Nullable
    public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        final ItemStackCapProvider provider = new ItemStackCapProvider(stack);
        provider.add("missile", ICBMClassicAPI.MISSILE_STACK_CAPABILITY, new CapabilityClusterMissileStack(stack));
        return provider;
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName()
    {
        return "missile.cluster";
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (tab == getCreativeTab() || tab == CreativeTabs.SEARCH)
        {
            items.add(createStack(new ItemStack(Items.ARROW), 200));
            items.add(createStack(new ItemStack(ItemReg.itemBomblet), 100));
        }
    }

    private ItemStack createStack(ItemStack projectile, int count) {
        final ItemStack clusterArrows = new ItemStack(this, 1);
        CapabilityClusterMissileStack cap = (CapabilityClusterMissileStack) clusterArrows.getCapability(ICBMClassicAPI.MISSILE_CAPABILITY, null);

        for(int i = 0; i < count; i++) {
            cap.getActionDataCluster().getClusterSpawnEntries().add(projectile.copy());
        }

        return clusterArrows;
    }

    @Override
    protected boolean hasDetailedInfo(ItemStack stack, EntityPlayer player)
    {
        return true;
    }

    @Override
    protected void getDetailedInfo(ItemStack stack, EntityPlayer player, List list)
    {
       //TODO display cluster information
    }
}
