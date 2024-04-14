package icbm.classic.content.blast.emp;

import icbm.classic.ICBMConstants;
import icbm.classic.api.actions.IActionData;
import icbm.classic.api.actions.cause.IActionSource;
import icbm.classic.api.actions.data.ActionField;
import icbm.classic.api.actions.data.ActionFields;
import icbm.classic.api.actions.data.ActionTypes;
import icbm.classic.api.actions.data.IActionFieldProvider;
import icbm.classic.api.data.meta.MetaTag;
import icbm.classic.config.blast.ConfigBlast;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ActionDataEmpArea implements IActionData<ActionEmpArea> {
    public static final MetaTag[] ACTION_TAGS = new MetaTag[]{ActionTypes.BLOCK_EDIT, ActionTypes.ENTITY_EDIT, ActionTypes.DESTRUCTIVE, ActionTypes.WORLD_AREA, ActionTypes.ENTITY_AREA};
    public static final ResourceLocation REG_NAME = new ResourceLocation(ICBMConstants.DOMAIN, "emp.area");
    /** @deprecated This is temp until registered */
    public static IActionData INSTANCE = new ActionDataEmpArea();

    @Nonnull
    public MetaTag[] getTypes() {
        return ACTION_TAGS;
    }

    @Nonnull
    @Override
    public ActionEmpArea create(World world, double x, double y, double z, @Nonnull IActionSource source, @Nullable IActionFieldProvider fieldAccessor) {
        final ActionEmpArea actionEmpArea = new ActionEmpArea(world, new Vec3d(x, y, z), source, this);
        if(fieldAccessor != null && fieldAccessor.hasField(ActionFields.BLAST_SIZE)) {
            actionEmpArea.setSize((int)Math.floor(fieldAccessor.getValue(ActionFields.BLAST_SIZE))); //TODO allow changing x, y, z size
        }
        else {
            actionEmpArea.setSize((int)Math.floor(ConfigBlast.emp.scale));
        }
        return actionEmpArea;
    }

    @Override
    public List<ActionField> getSupportedFields() {
        return ActionEmpArea.SUPPORTED_FIELDS;
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryKey() {
        return REG_NAME;
    }
}
