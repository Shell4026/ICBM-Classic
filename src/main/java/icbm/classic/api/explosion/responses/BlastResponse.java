package icbm.classic.api.explosion.responses;

import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.api.explosion.BlastState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;

/**
 * Created by Robin Seifert on 5/21/2021.
 * @deprecated Being replaced by {@link IActionStatus} <a href="https://builtbroken.codecks.io/decks/14-icbm-backlog/card/14e-replace-blastresponse-with-iactionstatus">CARD</a>
 */
@Deprecated
public final class BlastResponse implements IActionStatus
{
    public final BlastState state;
    public final ResourceLocation key;
    public final Throwable error;

    public BlastResponse(BlastState state, ResourceLocation key)
    {
        this(state, key, null);
    }

    public BlastResponse(BlastState state, ResourceLocation key, Throwable error)
    {
        this.state = state;
        this.key = key;
        this.error = error;
    }

    @Override
    public boolean isError() {
        return !state.good;
    }

    @Override
    public ITextComponent message() {
        return new TextComponentTranslation(key.toString());
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryKey() {
        return key;
    }
}
