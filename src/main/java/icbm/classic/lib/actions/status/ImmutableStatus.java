package icbm.classic.lib.actions.status;

import com.google.common.collect.ImmutableList;
import icbm.classic.api.actions.status.ActionStatusTypes;
import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.api.data.meta.MetaTag;
import lombok.Getter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

/**
 * Generic status messages that provide no details and are cached as static immutable versions
 */
public class ImmutableStatus implements IActionStatus {

    private final ResourceLocation regName;
    @Getter
    private final ImmutableList<MetaTag> typeTags;
    protected final String translationKey;

    protected ITextComponent textComponent;

    protected ImmutableStatus(ResourceLocation regName, MetaTag... tags) {
        this.regName = regName;
        this.typeTags = ImmutableList.copyOf(tags);
        this.translationKey = "info." + regName.toString();
    }

    public static ImmutableStatus blocking(ResourceLocation regName) {
        return new ImmutableStatus(regName, ActionStatusTypes.BLOCKING);
    }

    public static ImmutableStatus error(ResourceLocation regName) {
        return new ImmutableStatus(regName, ActionStatusTypes.BLOCKING, ActionStatusTypes.ERROR);
    }

    public static ImmutableStatus create(ResourceLocation regName, MetaTag... tags) {
        return new ImmutableStatus(regName, tags);
    }

    @Override
    public ITextComponent message() {
        if(textComponent == null) {
            textComponent = new TranslationTextComponent(translationKey);
        }
        return textComponent;
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryKey() {
        return regName;
    }

    @Override
    public String toString() {
        return "ActionStatus[ '" + getRegistryKey() + "' , '" + translationKey + "' ]@" + hashCode();
    }
}
