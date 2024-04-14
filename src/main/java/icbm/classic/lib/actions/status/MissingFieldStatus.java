package icbm.classic.lib.actions.status;

import icbm.classic.api.actions.status.ActionStatusTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public final class MissingFieldStatus extends ImmutableStatus {

    private final String source;
    private final String field;

    public MissingFieldStatus(ResourceLocation regName, String source, String field) {
        super(regName, ActionStatusTypes.BLOCKING, ActionStatusTypes.ERROR);
        this.source = source;
        this.field = field;
    }

    @Override
    public ITextComponent message() {
        if(textComponent == null) {
            textComponent = new TextComponentTranslation(translationKey, source, field);
        }
        return textComponent;
    }
}
