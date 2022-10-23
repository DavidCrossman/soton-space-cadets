package barebones;

import java.util.Deque;
import java.util.HashMap;

public final class Not extends Expression {
    private final Expression lhs, rhs;

    public Not(Expression lhs, Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String toString() {
        return "(%s not %s)".formatted(lhs, rhs);
    }

    @Override
    public Long evaluate(Deque<HashMap<String, Long>> state) {
        if (lhs.evaluate(state).equals(rhs.evaluate(state))) {
            return 0L;
        } else return 1L;
    }
}
