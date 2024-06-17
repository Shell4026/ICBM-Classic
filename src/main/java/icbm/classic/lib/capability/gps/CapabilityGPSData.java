package icbm.classic.lib.capability.gps;

import icbm.classic.api.caps.IGPSData;
import icbm.classic.lib.saving.NbtSaveHandler;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class CapabilityGPSData implements IGPSData, INBTSerializable<CompoundNBT> {

    private Vec3d position;
    private Integer dimension;

    @Override
    public void setPosition(@Nullable Vec3d position) {
        this.position = position;
    }

    @Override
    public void setWorld(@Nullable Integer dimension) {
        this.dimension = dimension;
    }

    @Nullable
    @Override
    public Vec3d getPosition() {
        return position;
    }

    @Nullable
    @Override
    public Integer getWorldId() {
        return dimension;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return SAVE_LOGIC.save(this);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        SAVE_LOGIC.load(this, nbt);
    }

    private static final NbtSaveHandler<IGPSData> SAVE_LOGIC = new NbtSaveHandler<IGPSData>()
        .mainRoot()
        /* */.nodeVec3d("pos", IGPSData::getPosition, IGPSData::setPosition)
        /* */.nodeInteger("dim", IGPSData::getWorldId, IGPSData::setWorld)
        .base();

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IGPSData.class, new Capability.IStorage<IGPSData>()
            {
                @Nullable
                @Override
                public INBT writeNBT(Capability<IGPSData> capability, IGPSData instance, Direction side) {
                    return SAVE_LOGIC.save(instance);
                }

                @Override
                public void readNBT(Capability<IGPSData> capability, IGPSData instance, Direction side, INBT nbt) {
                    if(nbt instanceof CompoundNBT) {
                        SAVE_LOGIC.load(instance, (CompoundNBT) nbt);
                    }
                }
            },
            CapabilityGPSData::new);
    }
}
