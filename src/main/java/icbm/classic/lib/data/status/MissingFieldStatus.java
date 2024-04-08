package icbm.classic.lib.data.status;

import icbm.classic.api.actions.status.IActionStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MissingFieldStatus extends ActionStatus {
    private final static String TRANSLATION = "info.icbmclassic:generic.error.missing.field";
    private final static String REG_NAME = "generic.error.missing.field";

    private final static Map<String, IActionStatus> cache = new HashMap<>();

    private String source;
    private String field;

    public MissingFieldStatus(String source, String field) {
        this.source = source;
        this.field = field;
    }

    public static IActionStatus get(String source, String field) {
        //TODO when less lazy move each instance to static fields for better visibility into errors
        final String key = source + "." + field;
        return cache.computeIfAbsent(key, (k) -> new MissingFieldStatus(source, field)
            .asError()
            .withRegName(REG_NAME)
            .withTranslation(TRANSLATION)
        );
    }

    @Override
    public ITextComponent message() {
        if(textComponent == null) {
            textComponent = new TextComponentTranslation(translationKey, source, field);
        }
        return textComponent;
    }
}
