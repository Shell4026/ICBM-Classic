package icbm.classic.content.actions;

import icbm.classic.api.actions.IAction;
import icbm.classic.api.actions.IActionData;
import icbm.classic.api.actions.cause.IActionSource;
import lombok.Data;
import lombok.Getter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Data
public abstract class ActionBase implements IAction {

    private final World world;
    private final Vec3d vec3d;
    private final IActionSource source;
    private final IActionData actionData;

    public BlockPos getPos()
    {
        return new BlockPos(vec3d);
    }

    @Override
    public double x() {
        return vec3d.x;
    }

    @Override
    public double y() {
        return vec3d.y;
    }

    @Override
    public double z() {
        return vec3d.z;
    }

    @Override
    public World world() {
        return world;
    }
}
