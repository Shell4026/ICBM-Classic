package icbm.classic.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import icbm.classic.ICBMConstants;
import icbm.classic.content.entity.EntityFragments;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class RenderFragments extends EntityRenderer<EntityFragments>
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(ICBMConstants.DOMAIN, "textures/entity/fragments/fragment.png");

    public RenderFragments(EntityRendererManager renderManager)
    {
        super(renderManager);
    }

    @Override
    public void doRender(EntityFragments entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.bindEntityTexture(entity);
        if (entity.isAnvil)
        {
            final BlockState blockState = Blocks.DAMAGED_ANVIL.getDefaultState()
                    .with(AnvilBlock.FACING, Direction.Plane.HORIZONTAL.random(entity.world.rand));
            //TODO store rotation and damage in entity to reduce random nature


            final BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();

            GlStateManager.pushMatrix();
            GlStateManager.translatef((float) x, (float) y + 0.5F, (float) z);


            this.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
            blockrendererdispatcher.renderBlockBrightness(blockState, entity.getBrightness());
            GlStateManager.translatef(0.0F, 0.0F, 1.0F);

            GlStateManager.popMatrix();
        }
        else
        {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.translatef((float) x, (float) y, (float) z);
            GlStateManager.rotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();

            GlStateManager.enableRescaleNormal();
            float f9 = (float) entity.arrowShake - partialTicks;

            if (f9 > 0.0F)
            {
                float f10 = -MathHelper.sin(f9 * 3.0F) * f9;
                GlStateManager.rotatef(f10, 0.0F, 0.0F, 1.0F);
            }

            GlStateManager.rotatef(45.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.scalef(0.05625F, 0.05625F, 0.05625F);
            GlStateManager.translatef(-4.0F, 0.0F, 0.0F);

            GlStateManager.normal3f(0.05625F, 0.0F, 0.0F);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(-7.0D, -2.0D, -2.0D).tex(0.0D, 0.15625D).endVertex();
            bufferbuilder.pos(-7.0D, -2.0D, 2.0D).tex(0.15625D, 0.15625D).endVertex();
            bufferbuilder.pos(-7.0D, 2.0D, 2.0D).tex(0.15625D, 0.3125D).endVertex();
            bufferbuilder.pos(-7.0D, 2.0D, -2.0D).tex(0.0D, 0.3125D).endVertex();
            tessellator.draw();

            GlStateManager.normal3f(-0.05625F, 0.0F, 0.0F);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(-7.0D, 2.0D, -2.0D).tex(0.0D, 0.15625D).endVertex();
            bufferbuilder.pos(-7.0D, 2.0D, 2.0D).tex(0.15625D, 0.15625D).endVertex();
            bufferbuilder.pos(-7.0D, -2.0D, 2.0D).tex(0.15625D, 0.3125D).endVertex();
            bufferbuilder.pos(-7.0D, -2.0D, -2.0D).tex(0.0D, 0.3125D).endVertex();
            tessellator.draw();

            for (int j = 0; j < 4; ++j)
            {
                GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.normal3f(0.0F, 0.0F, 0.05625F);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos(-8.0D, -2.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
                bufferbuilder.pos(8.0D, -2.0D, 0.0D).tex(0.5D, 0.0D).endVertex();
                bufferbuilder.pos(8.0D, 2.0D, 0.0D).tex(0.5D, 0.15625D).endVertex();
                bufferbuilder.pos(-8.0D, 2.0D, 0.0D).tex(0.0D, 0.15625D).endVertex();
                tessellator.draw();
            }

            GlStateManager.disableRescaleNormal();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }


        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityFragments entity)
    {
        return TEXTURE;
    }
}
