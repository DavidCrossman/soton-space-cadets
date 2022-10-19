package barebones;

import java.util.HashMap;

public final class Increment extends Statement {
    private final Identifier identifier;

    public Increment(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "Increment %s".formatted(identifier);
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
