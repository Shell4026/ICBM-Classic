package icbm.classic.content.entity.flyingblock;

import icbm.classic.ICBMClassic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashSet;

@SideOnly(Side.CLIENT)
public class RenderEntityBlock extends EntityRenderer<EntityFlyingBlock>
{
    private final HashSet<BlockState> failedBlocks = new HashSet();
    public RenderEntityBlock(EntityRendererManager renderManager)
    {
        super(renderManager);
        this.shadowSize = 0.5F;
    }

    @Override
    public void doRender(EntityFlyingBlock entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        BlockState blockState = entity.getBlockData().getBlockState();
        final BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.5F, (float) z);

        // If we previously failed try to use another state
        if(failedBlocks.contains(blockState)) {
            if(blockState.getMaterial() == Material.LEAVES) {
                blockState = net.minecraft.block.Blocks.LEAVES.getDefaultState();
            }
            else {
                blockState = Blocks.STONE.getDefaultState();
            }
        }

        try {
            this.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, 0.5F);
            blockrendererdispatcher.renderBlockBrightness(blockState, entity.getBrightness()); //TODO consider using item render instead?
            GlStateManager.translate(0.0F, 0.0F, 1.0F);
        }
        catch (Exception e) {
            if(!failedBlocks.contains(blockState)) {
                failedBlocks.add(blockState);

                // Log issue, user will likely never notice but still worth logging
                ICBMClassic.logger().error("Failed to render FlyingBlocks. This is likely an issue with the block being rendered. Please report the problem to the block's author."
                    + "\n Entity: " + entity
                    + "\n Block: " + entity.getBlockData().getBlockState()
                    , e);
            }
        }

        GlStateManager.popMatrix();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityFlyingBlock entity)
    {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}