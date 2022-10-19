package barebones.tree;

import java.util.HashMap;

public final class Increment extends Tree {
    private final Identifier identifier;

    public Increment(Tree identifier) {
        this.identifier = (Identifier) identifier;
    }

    @Override
    public String toString() {
        return "Increment %s".formatted(identifier);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public void execute(HashMap<String, Long> state) {
        Long val = identifier.evaluate(state);
        if (val == Long.MAX_VALUE) {
            throw new RuntimeException("Variable \"%s\" exceeded the maximum value of %s"
                    .formatted(identifier.getName(), Long.MAX_VALUE));
        }
        state.put(identifier.getName(), val + 1);
    }
}
