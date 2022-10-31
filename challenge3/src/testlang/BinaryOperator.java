package testlang;

import java.util.Deque;
import java.util.HashMap;

public final class BinaryOperator extends Expression {
    private final Expression lhs, rhs;
    private final java.util.function.BinaryOperator<Long> operator;

    public BinaryOperator(Expression lhs, Expression rhs, java.util.function.BinaryOperator<Long> operator) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.operator = operator;
    }

    @Override
    public Long evaluate(Deque<HashMap<String, Long>> state) {
        return operator.apply(lhs.evaluate(state), rhs.evaluate(state));
    }
}
