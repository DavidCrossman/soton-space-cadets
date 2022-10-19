package barebones.tree;

import java.util.HashMap;

public final class Not extends Expression {
    private final Expression lhs, rhs;

    public Not(Tree lhs, Tree rhs) {
        this.lhs = (Expression) lhs;
        this.rhs = (Expression) rhs;
    }

    @Override
    public String toString() {
        return "(%s not %s)".formatted(lhs, rhs);
    }

    public Tree getLhs() {
        return lhs;
    }

    public Tree getRhs() {
        return rhs;
    }

    @Override
    public void execute(HashMap<String, Long> state) {}

    @Override
    public Long evaluate(HashMap<String, Long> state) {
        if (lhs.evaluate(state).equals(rhs.evaluate(state))) {
            return 0L;
        } else return 1L;
    }
}
