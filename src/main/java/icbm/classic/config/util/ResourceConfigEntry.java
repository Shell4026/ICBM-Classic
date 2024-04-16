package icbm.classic.config.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.util.function.Function;

@Data
@AllArgsConstructor
public class ResourceConfigEntry<CONTENT, VALUE> implements Function<CONTENT, VALUE> {

    private Integer order;
    private Function<CONTENT, VALUE> function;

    @Override
    public VALUE apply(CONTENT content) {
        return function.apply(content);
    }
}
