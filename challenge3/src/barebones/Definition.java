package barebones;

import java.util.HashMap;

public class Definition extends Statement {
    private final Identifier identifier;
    private final Expression value;

    public Definition(Identifier identifier, Expression value) {
        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public String toString() {
        return value == null ? "Var %s".formatted(identifier) : "Var %s = %s".formatted(identifier, value);
    }

    @Override
    public void execute(HashMap<String, Long> state) {
        if (identifier.exists(state)) {
            throw new RuntimeException("Redefinition of variable \"%s\" in the scope"
                    .formatted(identifier.getName()));
        }
        state.put(identifier.getName(), value == null ? null : value.evaluate(state));
    }
}
