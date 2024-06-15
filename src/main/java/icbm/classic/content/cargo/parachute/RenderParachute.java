package icbm.classic.content.cargo.parachute;

import icbm.classic.client.render.entity.item.RenderItemImp;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;

public class RenderParachute extends RenderItemImp<EntityParachute> {
    public RenderParachute(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected void rotate(EntityParachute entity, float entityYaw, float entityPitch, float partialTicks) {
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        // TODO add wiggle to simulate wind
    }

    @Override
    protected ItemStack getRenderItem(EntityParachute entity) {
        return entity.getRenderItemStack();
    }

    @Override
    protected void scale(EntityParachute e, float partialTicks) {
        if(e != null) {
            final float scale = e.getRenderScale();
            GlStateManager.scale(scale, scale, scale);
        }
    }
}
