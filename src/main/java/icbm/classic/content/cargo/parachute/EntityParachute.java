package icbm.classic.content.cargo.parachute;

import icbm.classic.content.entity.flyingblock.EntityFlyingBlock;
import icbm.classic.content.reg.ItemReg;
import icbm.classic.lib.projectile.EntityProjectile;
import icbm.classic.lib.saving.NbtSaveHandler;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nonnull;

/**
 * Entity that acts as a slow falling seat for other entities to use
 */
public class EntityParachute extends EntityProjectile<EntityParachute> implements IEntityAdditionalSpawnData
{

    public static final float GRAVITY = 0.01f; // TODO config
    public static final float AIR_RESISTANCE = 0.95f; // TODO config
    public static final float ENTITY_SIZE = 0.5f;

    /** Stack to render */
    @Nonnull @Setter @Getter @Accessors(chain = true)
    private ItemStack renderItemStack = new ItemStack(ItemReg.itemParachute);

    /** Stack to drop on impact with ground */
    @Nonnull @Setter @Getter @Accessors(chain = true)
    private ItemStack dropItemStack = new ItemStack(ItemReg.itemParachute); // TODO consider used parachute item

    @Getter
    private float renderScale = 1;
    private float prevRenderScale = 1;


    public EntityParachute(World world)
    {
        super(world);
        this.setSize(ENTITY_SIZE, ENTITY_SIZE);
        this.preventEntitySpawning = true;
        this.ignoreFrustumCheck = true;
    }

    protected void setRenderScale(float scale) {
        this.renderScale = scale;
        if(Math.abs(prevRenderScale - scale) > 0.01) {
            this.prevRenderScale = this.renderScale;
            this.setSize(ENTITY_SIZE * scale, ENTITY_SIZE * scale);
        }
    }

    @Override
    protected float getImpactDamage(Entity entityHit, float velocity, RayTraceResult hit) {
        return 0; //TODO consider passenger weight? As heavy object on small parachute would be falling fast and do damage
    }

    @Override
    public void writeSpawnData(ByteBuf data)
    {
        data.writeInt(this.ticksInAir);
        ByteBufUtils.writeItemStack(data, renderItemStack);
    }

    @Override
    public void readSpawnData(ByteBuf data)
    {
        this.ticksInAir = data.readInt();
        renderItemStack = ByteBufUtils.readItemStack(data);
        // TODO pull iBakedModel and cache
    }

    @Override
    protected boolean shouldExpire() {
        return ticksInAir >= inAirKillTime || getPassengers().isEmpty();
    }

    @Override
    public double getMountedYOffset()
    {
        return -0.25;
    }

    @Override
    protected boolean canFitPassenger(Entity passenger)
    {
        return this.getPassengers().isEmpty();
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        this.setRenderScale(1);
    }

    @Override
    public void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if(passenger instanceof EntityItem)
        {
            this.setRenderScale(1);
        }
        else if(passenger instanceof EntityFlyingBlock)
        {
            this.setRenderScale(2);
        }
        else
        {
            this.setRenderScale(2);
        }
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        if (this.isPassenger(passenger))
        {
            if(passenger instanceof EntityItem)
            {
                if(((EntityItem) passenger).getItem().getItem() instanceof ItemBlock)
                {
                    passenger.setPosition(this.posX, this.posY - 0.6, this.posZ);
                }
                else
                {
                    passenger.setPosition(this.posX, this.posY - 0.7, this.posZ);
                }
            }
            else if(passenger instanceof EntityFlyingBlock)
            {
                passenger.setPosition(this.posX, this.posY - 1.5, this.posZ);
            }
            else
            {
                passenger.setPosition(this.posX, this.posY + passenger.height -0.25, this.posZ);
            }
        }
    }

    @Override
    protected float getGravity() {
        return GRAVITY; // TODO make dynamic based on passenger(s) and type
    }

    @Override
    protected float getAirResistance() {
        return AIR_RESISTANCE; // TODO make dynamic based on passenger(s) and type
    }

    @Override
    public boolean shouldRiderSit()
    {
        return false;
    }

    @Override
    protected boolean ignoreImpact(RayTraceResult hit) {
        // Ignore entity impacts, as we only care about the ground
        return hit.entityHit != null;
    }

    @Override
    protected boolean shouldCollideWith(Entity entity) {
        return super.shouldCollideWith(entity) && entity != shootingEntity;
    }

    @Override
    protected void onImpact(RayTraceResult impactLocation) {
        releaseParachute();
    }

    @Override
    protected void destroy() {
        this.releaseParachute();
    }

    protected void releaseParachute() {
        this.removePassengers();
        this.setDead(); //TODO have parachute drift away and then despawn with particles

        if(isServer() && this.dropItemStack != null && !this.dropItemStack.isEmpty()) {
            final EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY, this.posZ, this.dropItemStack.copy());
            entityitem.setDefaultPickupDelay();
            world.spawnEntity(entityitem);
        }

        //TODO add event, idea would be to use it non-projectile items and entities to handle additional logic
        //      though this is not meant to act as a replacement for other solutions. Such as spawn eggs using a deployer item.
        //      example, adding a parachute backpack to entities, or adding to player inventory
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag)
    {
        super.readEntityFromNBT(tag);
        SAVE_LOGIC.load(this, tag);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag)
    {
        super.writeEntityToNBT(tag);
        SAVE_LOGIC.save(this, tag);
    }

    private static final NbtSaveHandler<EntityParachute> SAVE_LOGIC = new NbtSaveHandler<EntityParachute>()
        .mainRoot()
        .nodeItemStack("renderItem", (e) -> e.renderItemStack, (e, i) -> e.renderItemStack = i)
        .nodeItemStack("dropItem", (e) -> e.dropItemStack, (e, i) -> e.dropItemStack = i)
        .base();
}