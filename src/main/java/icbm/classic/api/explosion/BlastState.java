package icbm.classic.api.explosion;

import icbm.classic.api.explosion.responses.BlastResponse;

/**
 * Created by Robin Seifert on 1/3/19.
 * @deprecated being replaced by {@link icbm.classic.api.actions.status.IActionStatus}
 */
@Deprecated
public enum BlastState
{
    /**
     * Triggered in main thread
     */
    TRIGGERED(true),

    /**
     * Triggered on the client
     */
    TRIGGERED_CLIENT(true),

    /**
     * Triggered in worker thread
     */
    THREADING(true),
    /**
     * Forge TNT event canceled blast
     */
    CANCELED(false),
    /**
     * Unexpected error
     */
    ERROR(false),
    /**
     * Blast was already triggered
     */
    ALREADY_TRIGGERED(true);

    public final boolean good;
    public final BlastResponse genericResponse;

    BlastState(boolean good)
    {
        this.good = good;
        this.genericResponse = new BlastResponse(this, null);
    }
}
