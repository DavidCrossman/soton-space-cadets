package testlang;

import java.util.Deque;
import java.util.HashMap;

public final class Block extends Statement {
    private final Program block;
    private final boolean isScoped;

    public Block(Program block, boolean isScoped) {
        this.block = block;
        this.isScoped = isScoped;
    }

    public Block(Program block) {
        this(block, true);
    }

    @Override
    public void execute(Deque<HashMap<String, Long>> state) {
        if (block == null) return;
        if (isScoped) state.push(new HashMap<>());
        block.execute(state);
        if (isScoped) state.pop();
    }
}
