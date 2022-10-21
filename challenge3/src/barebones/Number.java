package barebones;

import java.util.HashMap;

public final class Number extends Expression {
    private final Long value;

    public Number(Long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "'%s'".formatted(value);
    }

    @Override
    public Long evaluate(HashMap<String, Long> state) {
        return value;
    }
}
