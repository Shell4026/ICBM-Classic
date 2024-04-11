package icbm.classic.api.actions.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ActionFields {
    public static final ActionField<Float> BLAST_SIZE = new ActionField<Float>("blast.size", Float.class);
}
