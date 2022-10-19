package barebones.tree;

import java.util.HashMap;

public final class While extends Tree {
    private final Expression condition;
    private final Tree block;

    public While(Tree condition, Tree block) {
        this.condition = (Expression) condition; //TODO change parameter type
        this.block = block;
    }

    @Override
    public String toString() {
        return "While %s do {\n%s\n} end".formatted(condition, block == null ? "" : block);
    }

    public Tree getCondition() {
        return condition;
    }

    public Tree getBlock() {
        return block;
    }

    @Override
    public void execute(HashMap<String, Long> state) {
        while (!condition.evaluate(state).equals(0L)) {
            block.execute(state);
        }
    }
}
