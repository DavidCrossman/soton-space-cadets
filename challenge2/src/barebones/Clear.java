package barebones;

import java.util.HashMap;

public final class Clear extends Statement {
    private final Identifier identifier;

    public Clear(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "Clear %s".formatted(identifier);
    }

    @Override
    public void execute(HashMap<String, Long> state) {
        state.put(identifier.getName(), 0L);
    }
}
