package icbm.classic.content.missile.logic.source.cause;

import icbm.classic.ICBMConstants;
import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.actions.cause.IActionCause;
import icbm.classic.api.actions.cause.ICausedByBlock;
import icbm.classic.api.reg.obj.IBuilderRegistry;
import icbm.classic.lib.saving.NbtSaveHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nonnull;

/**
 * General purpose block cause
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CausedByBlock extends ActionCause implements ICausedByBlock {

    public static final ResourceLocation REG_NAME = new ResourceLocation(ICBMConstants.DOMAIN, "block");

    private World world;
    private BlockPos blockPos;
    private IBlockState blockState;

    private int worldId;

    public CausedByBlock(World world, BlockPos pos, IBlockState state) {
        this.world = world;
        this.worldId = world.provider.getDimension();
        this.blockPos = pos;
        this.blockState = state;
    }

    public World getWorld() {
        if(world == null) {
            world = DimensionManager.getWorld(worldId);
        }
        return world;
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryKey() {
        return REG_NAME;
    }

    @Nonnull
    @Override
    public IBuilderRegistry<IActionCause> getRegistry() {
        return ICBMClassicAPI.ACTION_CAUSE_REGISTRY;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return SAVE_LOGIC.save(this, super.serializeNBT());
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        SAVE_LOGIC.load(this, nbt);
    }

    private static final NbtSaveHandler<CausedByBlock> SAVE_LOGIC = new NbtSaveHandler<CausedByBlock>()
        .mainRoot()
        /* */.nodeInteger("level", CausedByBlock::getWorldId, CausedByBlock::setWorldId)
        /* */.nodeBlockPos("pos", CausedByBlock::getBlockPos, CausedByBlock::setBlockPos)
        /* */.nodeBlockState("state", CausedByBlock::getBlockState, CausedByBlock::setBlockState)
        .base();
}
