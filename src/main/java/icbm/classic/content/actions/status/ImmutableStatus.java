package icbm.classic.content.actions.status;

import icbm.classic.api.actions.status.IActionStatus;
import lombok.NoArgsConstructor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;

/**
 * Generic status messages that provide no details and are cached as static immutable versions
 */
public class ImmutableStatus implements IActionStatus {

    private final ResourceLocation regName;
    protected final String translationKey;
    private final boolean isError;
    private final boolean isBlocking;

    protected ITextComponent textComponent;

    protected ImmutableStatus(ResourceLocation regName, boolean isError, boolean isBlocking) {
        this.regName = regName;
        this.translationKey = "info." + regName.toString();
        this.isError = isError;
        this.isBlocking = isBlocking;
    }

    public static ImmutableStatus blocking(ResourceLocation regName) {
        return new ImmutableStatus(regName, false, true);
    }

    public static ImmutableStatus error(ResourceLocation regName) {
        return new ImmutableStatus(regName, true, false);
    }

    public static ImmutableStatus create(ResourceLocation regName) {
        return new ImmutableStatus(regName, false, false);
    }

    @Override
    public boolean isError() {
        return isError;
    }

    @Override
    public boolean isBlocking() {
        return isError() || isBlocking;
    }

    @Override
    public ITextComponent message() {
        if(textComponent == null) {
            textComponent = new TextComponentTranslation(translationKey);
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
