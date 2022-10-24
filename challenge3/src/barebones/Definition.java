package barebones;

import java.util.Deque;
import java.util.HashMap;

public class Definition extends Statement {
    private final Identifier identifier;
    private final Expression value;

    public Definition(Identifier identifier, Expression value) {
        this.identifier = identifier;
        this.value = value;
    }

    public Definition(Identifier identifier) {
        this(identifier, null);
    }

    @Override
    public String toString() {
        return value == null ? "Var %s".formatted(identifier) : "Var %s = %s".formatted(identifier, value);
    }

    @Override
    public void execute(Deque<HashMap<String, Long>> state) {
        identifier.define(state, value == null ? null : value.evaluate(state));
    }
}
