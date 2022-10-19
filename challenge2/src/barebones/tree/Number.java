package barebones.tree;

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

    public Long getValue() {
        return value;
    }

    @Override
    public void execute(HashMap<String, Long> state) {}

    @Override
    public Long evaluate(HashMap<String, Long> state) {
        return value;
    }
}
