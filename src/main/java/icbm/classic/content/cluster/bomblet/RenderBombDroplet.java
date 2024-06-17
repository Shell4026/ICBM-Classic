package icbm.classic.content.cluster.bomblet;

import icbm.classic.ICBMConstants;
import icbm.classic.client.render.entity.RenderEntityItem2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderBombDroplet extends EntityRenderer<EntityBombDroplet>
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(ICBMConstants.DOMAIN, "textures/entity/fragments/fragment.png");

    private final ItemEntity entityItem;
    private final RenderEntityItem2 renderEntityItem;
    public RenderBombDroplet(EntityRendererManager renderManager)
    {
        super(renderManager);
        entityItem = new ItemEntity(null);
        renderEntityItem = new RenderEntityItem2(renderManager, Minecraft.getMinecraft().getRenderItem(), ItemCameraTransforms.TransformType.NONE);
    }

    @Override
    public void doRender(EntityBombDroplet entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.bindEntityTexture(entity);

        //Setup
        GlStateManager.pushMatrix();

        //Translate to center of entity collider
        GlStateManager.translate(x, y + 0.15, z);

        //Rotate
        float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 180;
        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks + 90;
        GlStateManager.rotate(yaw, 0F, 1F, 0F);
        GlStateManager.rotate(pitch, 1F, 0F, 0F);

        //Translate to rotation point of model TODO extract from model file
        GlStateManager.translate(0, 0.2, 0);

        //Render missile
        renderMissile(entity.toStack(),
            entity.world, entity.posX, entity.posY, entity.posZ,
            0, 0, 0, entityYaw, partialTicks);

        //Reset
        GlStateManager.popMatrix();


        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public void renderMissile(ItemStack missileStack, World world, double wx, double wy, double wz,
                              double x, double y, double z, float entityYaw, float partialTicks)
    {
        //Set data for fake entity
        entityItem.setWorld(world);
        entityItem.rotationYaw = 0;
        entityItem.setPosition(wx, wy, wz);
        entityItem.setItem(missileStack);

        //render entity item
        renderEntityItem.doRender(entityItem, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityBombDroplet entity)
    {
        return TEXTURE;
    }
}
