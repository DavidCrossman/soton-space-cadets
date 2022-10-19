package barebones.tree;

import java.util.HashMap;

public final class Decrement extends Tree {
    private final Identifier identifier;

    public Decrement(Tree identifier) {
        this.identifier = (Identifier) identifier;
    }

    @Override
    public String toString() {
        return "Decrement %s".formatted(identifier);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public void execute(HashMap<String, Long> state) {
        Long val = identifier.evaluate(state);
        if (val <= 0) {
            throw new RuntimeException("Attempted to reduce variable \"%s\" below zero"
                    .formatted(identifier.getName()));
        }
        state.put(identifier.getName(), val - 1);
    }
}
