package barebones;

import java.util.Deque;
import java.util.HashMap;

public final class If extends Statement {
    private final Expression condition;
    private final Program ifBlock, elseBlock;

    public If(Expression condition, Program ifBlock, Program elseBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    public If(Expression condition, Program ifBlock) {
        this(condition, ifBlock, null);
    }

    @Override
    public String toString() {
        return elseBlock == null ? "if %s then \n%s\nend".formatted(condition, ifBlock == null ? "" : ifBlock)
                : "if %s then \n%s\nelse\n%s\nend".formatted(condition, ifBlock == null ? "" : ifBlock, elseBlock);
    }

    @Override
    public void execute(Deque<HashMap<String, Long>> state) {
        if (!condition.evaluate(state).equals(0L)) {
            if (ifBlock != null) ifBlock.execute(state);
        } else if (elseBlock != null) {
            elseBlock.execute(state);
        }
    }
}
