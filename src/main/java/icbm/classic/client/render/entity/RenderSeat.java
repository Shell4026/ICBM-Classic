package icbm.classic.client.render.entity;

import icbm.classic.content.entity.EntityPlayerSeat;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@OnlyIn(Dist.CLIENT)
public class RenderSeat extends EntityRenderer<EntityPlayerSeat>
{
    public RenderSeat(EntityRendererManager renderManager)
    {
        super(renderManager);
        this.shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntityPlayerSeat seat, double x, double y, double z, float entityYaw, float partialTicks)
    {
        super.doRender(seat, x, y, z, entityYaw, partialTicks);
        //GlStateManager.pushMatrix();
        //renderOffsetAABB(seat.getEntityBoundingBox(), x - seat.lastTickPosX, y - seat.lastTickPosY, z - seat.lastTickPosZ);
        //GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPlayerSeat entity)
    {
        return null;
    }
}