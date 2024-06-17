package icbm.classic.lib.actions.status;

import icbm.classic.ICBMConstants;
import icbm.classic.api.actions.status.ActionStatusTypes;
import icbm.classic.lib.saving.NbtSaveHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.INBTSerializable;

public final class MissingFieldStatus extends ImmutableStatus implements INBTSerializable<CompoundNBT> {

    public static final ResourceLocation REG_NAME = new ResourceLocation(ICBMConstants.DOMAIN, "missing.field");

    @Setter @Getter @Accessors(chain = true)
    private String source;
    @Setter @Getter @Accessors(chain = true)
    private String field;

    public MissingFieldStatus() {
        super(REG_NAME, ActionStatusTypes.BLOCKING, ActionStatusTypes.ERROR);
    }

    public MissingFieldStatus(ResourceLocation regName, String source, String field) {
        super(regName, ActionStatusTypes.BLOCKING, ActionStatusTypes.ERROR);
        this.source = source;
        this.field = field;
    }

    @Override
    public ITextComponent message() {
        if(textComponent == null) {
            textComponent = new TranslationTextComponent(translationKey, source, field);
        }
        return textComponent;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return SAVE_LOGIC.save(this);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        SAVE_LOGIC.load(this, nbt);
    }

    private static final NbtSaveHandler<MissingFieldStatus> SAVE_LOGIC = new NbtSaveHandler<MissingFieldStatus>()
        .mainRoot()
        /* */.nodeString("source", MissingFieldStatus::getSource, MissingFieldStatus::setSource)
        /* */.nodeString("field", MissingFieldStatus::getField, MissingFieldStatus::setField)
        .base();
}
