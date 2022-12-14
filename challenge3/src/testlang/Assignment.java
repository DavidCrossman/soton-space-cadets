package testlang;

import java.util.Deque;
import java.util.HashMap;

public final class Assignment extends Statement {
    private final Identifier identifier;
    private final Expression value;

    public Assignment(Identifier identifier, Expression value) {
        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public void execute(Deque<HashMap<String, Long>> state) {
        identifier.assign(state, value.evaluate(state));
    }
}
