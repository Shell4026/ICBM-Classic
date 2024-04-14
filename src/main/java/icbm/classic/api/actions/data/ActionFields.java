package icbm.classic.api.actions.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ActionFields {
    public static final ActionField<Float> AREA_SIZE = new ActionField<Float>("area.size", Float.class);
}
