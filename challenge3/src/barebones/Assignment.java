package barebones;

import java.util.HashMap;

public class Assignment extends Statement {
    private final Identifier identifier;
    private final Expression value;

    public Assignment(Identifier identifier, Expression value) {
        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public String toString() {
        return "%s = %s".formatted(identifier, value);
    }

    @Override
    public void execute(HashMap<String, Long> state) {
        identifier.checkExists(state);
        state.put(identifier.getName(), value.evaluate(state));
    }
}
