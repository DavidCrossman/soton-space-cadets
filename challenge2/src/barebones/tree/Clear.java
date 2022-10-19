package barebones.tree;

import java.util.HashMap;

public final class Clear extends Tree {
    private final Identifier identifier;

    public Clear(Tree identifier) {
        this.identifier = (Identifier) identifier;
    }

    @Override
    public String toString() {
        return "Clear %s".formatted(identifier);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public void execute(HashMap<String, Long> state) {
        state.put(identifier.getName(), 0L);
    }
}
