package icbm.classic.client.render.entity.item;

import icbm.classic.lib.data.LazyBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Supplier;

/**
 * EntityItem style render
 *
 * @param <E> to render
 */
@SideOnly(Side.CLIENT)
public abstract class RenderItemImp<E extends Entity> extends Render<E>
{
    private final RenderItem itemRenderer;
    private final Random random = new Random();

    private static final Supplier<ItemStack> BACKUP_RENDER_STACK = new LazyBuilder<>(() -> new ItemStack(Blocks.FIRE));

    @Setter @Getter
    @Accessors(chain = true)
    protected boolean billboard = false;

    public RenderItemImp(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
        this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    protected abstract ItemStack getRenderItem(E entity);

    protected ItemCameraTransforms.TransformType getTransformType() {
        return ItemCameraTransforms.TransformType.NONE;
    }

    protected void translate(E entity, IBakedModel iBakedModel, double x, double y, double z) {
        float hoverStart = iBakedModel.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
        GlStateManager.translate((float)x, (float)y + 0.25F * hoverStart, (float)z);
    }

    protected void rotate(E entity, float entityYaw) {
        // Rotate by entity yaw
        if(billboard) {
            GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F); //fish ><>
        }
        else
        {
            GlStateManager.rotate(entityYaw, 0.0F, 1.0F, 0.0F);
        }
    }

    protected void scale(E e) {
        //GlStateManager.scale(2, 2, 2);
    }

    @Override
    public void doRender(@Nonnull E entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        ItemStack itemstack = getRenderItem(entity);
        if(itemstack == null || itemstack.isEmpty()) {
            itemstack = BACKUP_RENDER_STACK.get();
        }

        this.renderItem(entity, itemstack, x, y, z, entityYaw, partialTicks);

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    protected void renderItem(E entity, ItemStack itemstack, double x, double y, double z, float entityYaw, float partialTicks) {
        this.random.setSeed(Item.getIdFromItem(itemstack.getItem()) + itemstack.getMetadata());
        boolean hasTexture = false;

        if (this.bindEntityTexture(entity))
        {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
            hasTexture = true;
        }

        //Setup
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();

        // TODO may need optimization, upcraft suggests caching model as doing the lookup per frame is slow.. could do per entity? or tree(item -> key -> model)
        IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, entity.world, (EntityLivingBase) null);
        this.translate(entity, ibakedmodel, x, y, z);
        this.rotate(entity, entityYaw);
        this.scale(entity);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        // Render item
        GlStateManager.pushMatrix();
        ibakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, getTransformType(), false);
        this.itemRenderer.renderItem(itemstack, ibakedmodel);
        GlStateManager.popMatrix();

        // Reset
        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        this.bindEntityTexture(entity);

        if (hasTexture)
        {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
        }
    }

    @Override
    @Nonnull
    protected ResourceLocation getEntityTexture(@Nonnull E entity)
    {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}