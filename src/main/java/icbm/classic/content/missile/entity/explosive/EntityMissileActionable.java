package icbm.classic.content.missile.entity.explosive;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.actions.IActionData;
import icbm.classic.api.reg.IExplosiveData;
import icbm.classic.config.missile.ConfigMissile;
import icbm.classic.content.missile.entity.EntityMissile;
import icbm.classic.content.missile.logic.TargetRangeDet;
import icbm.classic.content.missile.logic.source.ActionSource;
import icbm.classic.content.missile.logic.source.cause.EntityCause;
import icbm.classic.lib.actions.PotentialAction;
import icbm.classic.lib.capability.ex.CapabilityExplosiveEntity;
import icbm.classic.lib.saving.NbtSaveHandler;
import icbm.classic.lib.saving.NbtSaveNode;
import icbm.classic.prefab.entity.EntityICBM;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Missile with generic action handling
 */
public class EntityMissileActionable extends EntityMissile<EntityMissileActionable>
{
    /** Explosive data and settings */
    @Getter
    private final PotentialAction mainAction = new PotentialAction();
    @Getter @Setter
    private ItemStack originalStack = ItemStack.EMPTY;

    private static final DataParameter<Float> MAX_HEALTH = EntityDataManager.<Float>createKey(EntityMissileActionable.class, DataSerializers.FLOAT);

    public EntityMissileActionable(World w)
    {
        super(w);
        this.setSize(.5F, .5F);
        this.inAirKillTime = 144000 /* 2 hours */;
        this.isImmuneToFire = true;
        this.ignoreFrustumCheck = true;

        // Init health as explosive field is not set at time of health registration
        setHealth(getMaxHealth());
    }

    @Override
    protected void entityInit()
    {
        this.dataManager.register(MAX_HEALTH, (float) ConfigMissile.TIER_1_HEALTH);
    }

    @Override
    protected void onDestroyedBy(DamageSource source, float damage)
    {
       super.onDestroyedBy(source, damage);
       // TODO add config
       // TODO add random chance modifier
       if(source.isExplosion() || source.isFireDamage()) {
           this.mainAction.doAction(getEntityWorld(), posX, posY, posZ, new EntityCause(this)); // Add damage source cause
       }
    }

    @Override
    public String getName()
    {
        final IActionData data = mainAction.getActionData();
        if (data != null)
        {
            return I18n.translateToLocal("missile." + data.getRegistryKey().toString() + ".name");
        }
        return I18n.translateToLocal("missile.icbmclassic:generic.name");
    }

    @Override
    public void writeSpawnData(ByteBuf additionalMissileData)
    {
        final NBTTagCompound saveData = SAVE_LOGIC.save(this, new NBTTagCompound());
        ByteBufUtils.writeTag(additionalMissileData, saveData);
        super.writeSpawnData(additionalMissileData);
    }

    @Override
    public void readSpawnData(ByteBuf additionalMissileData)
    {
        final NBTTagCompound saveData = ByteBufUtils.readTag(additionalMissileData);
        SAVE_LOGIC.load(this, saveData);
        super.readSpawnData(additionalMissileData);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        this.mainAction.update(ticksExisted, !this.getEntityWorld().isRemote);
    }

    @Override
    public boolean processInitialInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand)
    {
        //Allow missile to override interaction
        if (ICBMClassicAPI.EX_MISSILE_REGISTRY.onInteraction(this, player, hand))
        {
            return true;
        }
        return super.processInitialInteract(player, hand);
    }

    @Override
    protected void onImpact(RayTraceResult impactLocation) {
        super.onImpact(impactLocation);
        // TODO add impact cause
        mainAction.doAction(getEntityWorld(), impactLocation.hitVec.x, impactLocation.hitVec.y, impactLocation.hitVec.z, new EntityCause(this));
    }

    @Override
    public ItemStack toStack() {
        return originalStack;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        SAVE_LOGIC.load(this, nbt);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        SAVE_LOGIC.save(this, nbt);
    }

    private static final NbtSaveHandler<EntityMissileActionable> SAVE_LOGIC = new NbtSaveHandler<EntityMissileActionable>()
        .mainRoot()
        /* */.nodeINBTSerializable("potential_action", EntityMissileActionable::getMainAction)
        /* */.nodeItemStack("original_stack", EntityMissileActionable::getOriginalStack, EntityMissileActionable::setOriginalStack)
        .base();
}
