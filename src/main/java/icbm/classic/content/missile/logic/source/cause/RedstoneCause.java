package icbm.classic.content.missile.logic.source.cause;

import icbm.classic.ICBMConstants;
import icbm.classic.lib.saving.NbtSaveHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RedstoneCause extends CausedByBlock {

    public static final ResourceLocation REG_NAME = new ResourceLocation(ICBMConstants.DOMAIN, "block.redstone");

    private Direction side;

    public RedstoneCause(World world, BlockPos pos, BlockState state, Direction side) {
        super(world, pos, state);
        this.side = side;
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryKey() {
        return REG_NAME;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return SAVE_LOGIC.save(this, super.serializeNBT());
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        SAVE_LOGIC.load(this, nbt);
    }

    private static final NbtSaveHandler<RedstoneCause> SAVE_LOGIC = new NbtSaveHandler<RedstoneCause>()
        .mainRoot()
        /* */.nodeFacing("side", RedstoneCause::getSide, RedstoneCause::setSide)
        .base();
}
