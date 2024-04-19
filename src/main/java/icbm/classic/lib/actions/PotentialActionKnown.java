package icbm.classic.lib.actions;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.actions.IActionData;
import icbm.classic.api.actions.IActionProvider;
import icbm.classic.api.actions.data.ActionField;
import icbm.classic.api.actions.data.IActionFieldProvider;
import icbm.classic.lib.data.LazyBuilder;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Pre-built action for general purpose implementation where action is known
 */
public final class PotentialActionKnown extends PotentialActionImp<PotentialActionKnown> implements IActionFieldProvider {

    private final LazyBuilder<IActionData> actionData;

    public PotentialActionKnown(ResourceLocation key) {
        this.actionData = new LazyBuilder<>(() -> ICBMClassicAPI.ACTION_REGISTRY.getOrBuild(key));
    }

    @Nonnull
    @Override
    public IActionData getActionData() {
        return actionData.get();
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryKey() {
        return null;
    }
}
