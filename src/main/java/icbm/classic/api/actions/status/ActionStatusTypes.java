package icbm.classic.api.actions.status;

import icbm.classic.api.data.meta.MetaTag;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.util.ResourceLocation;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ActionStatusTypes {

    /** Root for all actions */
    public static final MetaTag ROOT = MetaTag.create(new ResourceLocation("icbmclassic","action.status"));

    /** Status that is providing an error */
    public static final MetaTag ERROR = MetaTag.create(ROOT, "error");

    /** Status that is providing a warning */
    public static final MetaTag WARNING = MetaTag.create(ROOT, "warn");

    /** Status that prevents interaction from continuing */
    public static final MetaTag BLOCKING = MetaTag.create(ROOT, "blocking");
}
