package barebones.tree;

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
    public void execute(HashMap<String, Long> state) {}

    @Override
    public Long evaluate(HashMap<String, Long> state) {
        return state.get(name);
    }
}
