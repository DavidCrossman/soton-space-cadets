package testlang;

import java.util.Deque;
import java.util.HashMap;

public final class UnaryOperator extends Expression {
    private final Expression term;
    private final java.util.function.UnaryOperator<Long> operator;

    public UnaryOperator(Expression term, java.util.function.UnaryOperator<Long> operator) {
        this.term = term;
        this.operator = operator;
    }

    @Override
    public Long evaluate(Deque<HashMap<String, Long>> state) {
        return operator.apply(term.evaluate(state));
    }
}
