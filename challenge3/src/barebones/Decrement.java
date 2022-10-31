package barebones;

import java.util.Deque;
import java.util.HashMap;

public final class Decrement extends Statement {
    private final Identifier identifier;

    public Decrement(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "Decrement %s".formatted(identifier);
    }

    @Override
    public void execute(Deque<HashMap<String, Long>> state) {
        Long val = identifier.evaluate(state);
        if (val <= 0) {
            throw new RuntimeException("Attempted to reduce variable \"%s\" below zero"
                    .formatted(identifier.getName()));
        }
        identifier.assign(state, val - 1);
    }
}
