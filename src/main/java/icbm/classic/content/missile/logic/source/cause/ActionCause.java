package icbm.classic.content.missile.logic.source.cause;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.actions.cause.IActionCause;
import icbm.classic.lib.saving.NbtSaveHandler;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class ActionCause implements IActionCause, INBTSerializable<CompoundNBT> {

    private IActionCause parent;

    @Setter @Getter
    private String name;

    public IActionCause setPreviousCause(IActionCause parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public IActionCause getPreviousCause() {
        return parent;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return SAVE_LOGIC.save(this, new CompoundNBT());
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        SAVE_LOGIC.load(this, nbt);
    }

    private static final NbtSaveHandler<ActionCause> SAVE_LOGIC = new NbtSaveHandler<ActionCause>()
        .mainRoot()
        /* */.nodeString("name", ActionCause::getName, ActionCause::setName)
        /* */.nodeBuildableObject("parent", () -> ICBMClassicAPI.ACTION_CAUSE_REGISTRY, ActionCause::getPreviousCause, ActionCause::setPreviousCause)
        .base();
}
