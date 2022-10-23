package barebones;

import java.util.Deque;
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
    public void execute(Deque<HashMap<String, Long>> state) {
        if (identifier.exists(state)) {
            identifier.assign(state, 0L);
        } else {
            identifier.define(state, 0L);
        }
    }
}
