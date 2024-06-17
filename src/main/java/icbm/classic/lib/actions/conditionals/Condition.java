package icbm.classic.lib.actions.conditionals;

import icbm.classic.api.actions.conditions.ICondition;
import icbm.classic.lib.saving.NbtSaveHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class Condition implements ICondition, INBTSerializable<CompoundNBT> {
    /**
     * Display name
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    private String name;

    @Override
    public ITextComponent getDisplayName() {
        if (name != null) {
            return new TranslationTextComponent(getTranslationKey() + ".user_defined_name", name);
        }
        return new TranslationTextComponent(getTranslationKey());
    }

    @Override
    public CompoundNBT serializeNBT() {
        return SAVE_LOGIC.save(this);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        SAVE_LOGIC.load(this, nbt);
    }

    private static final NbtSaveHandler<Condition> SAVE_LOGIC = new NbtSaveHandler<Condition>()
        .mainRoot()
        /* */.nodeString("display_name", Condition::getName, Condition::setName)
        .base();
}
