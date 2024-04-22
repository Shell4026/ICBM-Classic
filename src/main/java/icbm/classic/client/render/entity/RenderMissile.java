package icbm.classic.client.render.entity;

import icbm.classic.ICBMClassic;
import icbm.classic.client.render.entity.item.RenderItemImp;
import icbm.classic.content.missile.entity.EntityMissile;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderMissile extends RenderItemImp<EntityMissile>
{
    public static RenderMissile INSTANCE;

    public RenderMissile(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Override
    protected ItemStack getRenderItem(EntityMissile entity) {
        return entity.toStack();
    }

    @Override
    protected void translate(@Nullable EntityMissile entity, IBakedModel iBakedModel, double x, double y, double z, float partialTicks) {
        //Translate to center of entity collider
        if(entity != null) {
            GlStateManager.translate(x, y + 0.2, z); //TODO handle in JSON
        }
        else {
            GlStateManager.translate(x, y, z);
        }
    }

    @Override
    protected float getYaw(@Nonnull EntityMissile entityMissile, float providedYaw, float partialTicks) {
        return providedYaw - 180; //TODO handle offset in JSON
    }

    @Override
    protected float getPitch(@Nonnull EntityMissile entityMissile, float providedPitch, float partialTicks) {
        return  providedPitch - 90; //TODO handle offset in JSON
    }

    @Override
    protected void rotate(@Nullable EntityMissile entityMissile, float entityYaw, float entityPitch, float partialTicks) {
        //Rotate
        GlStateManager.rotate(entityYaw, 0F, 1F, 0F);
        GlStateManager.rotate(entityPitch, 1F, 0F, 0F);

        //Translate to rotation point of model TODO handle in JSON
        if(entityMissile != null) {
            GlStateManager.translate(0, -0.8, 0);
        }
    }

    @Override
    public void doRender(EntityMissile entityMissile, double x, double y, double z, float entityYaw, float partialTicks)
    {
        super.doRender(entityMissile, x, y, z, entityYaw, partialTicks);

        ICBMClassic.logger().info("Render: {} -> {},{},{}", entityMissile, x, y, z);

        if (renderManager.isDebugBoundingBox()) //TODO fix so we can see motion vector
        {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos(x, y, z).color(0, 255, 0, 255).endVertex();
            bufferbuilder.pos(x + entityMissile.motionX * 2.0D, y + entityMissile.motionY * 2.0D, z + entityMissile.motionZ * 2.0D).color(0, 255, 0, 2555).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
        }
    }
}