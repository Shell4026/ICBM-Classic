package icbm.classic.content.actions;

import icbm.classic.api.actions.IAction;
import icbm.classic.api.actions.IActionData;
import icbm.classic.api.actions.cause.IActionSource;
import lombok.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Getter @Setter @RequiredArgsConstructor
public abstract class ActionBase implements IAction {

    private final World world;
    private final Vec3d position;
    private final IActionSource source;
    private final IActionData actionData;

    private BlockPos pos;

    public BlockPos getBlockPos()
    {
        if(pos == null) {
            pos = new BlockPos(position);
        }
        return pos;
    }
}
