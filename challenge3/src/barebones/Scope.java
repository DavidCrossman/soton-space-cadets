package barebones;

import java.util.Deque;
import java.util.HashMap;

public class Scope extends Statement {
    private final Program block;

    public Scope(Program block) {
        this.block = block;
    }

    @Override
    public String toString() {
        return "{\n%s\n}".formatted(block == null ? "" : block);
    }

    @Override
    public void execute(Deque<HashMap<String, Long>> state) {
        if (block == null) return;
        state.push(new HashMap<>());
        block.execute(state);
        state.pop();
    }
}
