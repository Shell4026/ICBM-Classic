package icbm.classic.client.render.entity;

import icbm.classic.content.entity.EntitySmoke;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSmoke extends EntityRenderer<EntitySmoke>
{
    public RenderSmoke(EntityRendererManager renderManager)
    {
        super(renderManager);
        this.shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntitySmoke seat, double x, double y, double z, float entityYaw, float partialTicks)
    {
        super.doRender(seat, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySmoke entity)
    {
        return null;
    }
}