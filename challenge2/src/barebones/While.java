package barebones;

import java.util.HashMap;

public final class While extends Statement {
    private final Expression condition;
    private final Program block;

    public While(Expression condition, Program block) {
        this.condition = condition;
        this.block = block;
    }

    @Override
    public String toString() {
        return "While %s do {\n%s\n} end".formatted(condition, block == null ? "" : block);
    }

    @Override
    public void execute(HashMap<String, Long> state) {
        while (!condition.evaluate(state).equals(0L)) {
            if (block != null) block.execute(state);
        }
    }
}
