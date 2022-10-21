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

    public boolean exists(HashMap<String, Long> state) {
        return state.containsKey(name);
    }

    public void checkExists(HashMap<String, Long> state) {
        if (!exists(state)) {
            throw new RuntimeException("Variable \"%s\" does not exist".formatted(name));
        }
    }

    @Override
    public Long evaluate(HashMap<String, Long> state) {
        checkExists(state);
        Long val = state.get(name);
        if (val == null) {
            throw new RuntimeException("Variable \"%s\" is not defined".formatted(name));
        }
        return val;
    }
}
