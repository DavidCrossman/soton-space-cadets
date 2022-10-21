package barebones;

import java.util.HashMap;

public final class Identifier extends Expression {
    private final String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "\"%s\"".formatted(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public Long evaluate(HashMap<String, Long> state) {
        Long val = state.get(name);
        if (val == null) {
            throw new RuntimeException("Variable \"%s\" is not defined".formatted(name));
        }
        return val;
    }
}
