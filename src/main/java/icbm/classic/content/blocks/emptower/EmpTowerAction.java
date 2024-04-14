package icbm.classic.content.blocks.emptower;

import icbm.classic.api.actions.IActionData;
import icbm.classic.api.actions.IPotentialAction;
import icbm.classic.api.actions.cause.IActionCause;
import icbm.classic.api.actions.data.ActionField;
import icbm.classic.api.actions.data.ActionFields;
import icbm.classic.api.actions.data.IActionFieldProvider;
import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.api.refs.ICBMExplosives;
import icbm.classic.content.missile.logic.source.ActionSource;
import icbm.classic.content.missile.logic.source.cause.CausedByBlock;
import lombok.Data;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class EmpTowerAction implements IPotentialAction, IActionFieldProvider {

    private static final List<ActionField> SUPPORTED_FIELDS = new ArrayList(Collections.singleton(ActionFields.AREA_SIZE));

    private final TileEMPTower host;
    public <T> T getValue(ActionField<T> key) {
        if(key == ActionFields.AREA_SIZE) {
            return key.cast((float)host.getRange());
        }
        return null;
    }

    public List<ActionField> getFields() {
        return SUPPORTED_FIELDS;
    }

    public <T> boolean hasField(ActionField<T> key) {
        return SUPPORTED_FIELDS.contains(key);
    }

    @Nonnull
    @Override
    public IActionData getActionData() {
        return ICBMExplosives.EMP;
    }

    @Nonnull
    @Override
    public IActionStatus checkAction(World world, double x, double y, double z, @Nullable IActionCause cause) {
        return null;
    }

    @Nonnull
    @Override
    public IActionStatus doAction(World world, double x, double y, double z, @Nullable IActionCause cause) {
        final CausedByBlock selfCause = new CausedByBlock(world, host.getPos(), host.getBlockState()); // TODO add caused by such as redstone, remote, etc
        final ActionSource source = new ActionSource(world, new Vec3d(x, y, z), selfCause.setPreviousCause(cause));
        return ICBMExplosives.EMP.create(world, x, y, z, source, this).doAction();
    }
}
