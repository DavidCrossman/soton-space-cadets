package barebones;

import java.util.Deque;
import java.util.HashMap;

public final class Comparison extends Expression {
    public enum Operator {
        IS,
        NOT,
        LESS_EQ,
        GREATER_EQ,
        LESS,
        GREATER,
    }
    private final Expression lhs, rhs;
    private final Operator operator;

    public Comparison(Expression lhs, Expression rhs, Operator operator) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "(%s %s %s)".formatted(lhs, switch (operator) {
            case IS -> "==";
            case NOT -> "!=";
            case LESS_EQ -> "<=";
            case GREATER_EQ -> ">=";
            case LESS -> "<";
            case GREATER -> ">";
        }, rhs);
    }

    @Override
    public Long evaluate(Deque<HashMap<String, Long>> state) {
        return switch (operator) {
            case IS -> lhs.evaluate(state).equals(rhs.evaluate(state));
            case NOT -> !lhs.evaluate(state).equals(rhs.evaluate(state));
            case LESS_EQ -> lhs.evaluate(state) <= rhs.evaluate(state);
            case GREATER_EQ -> lhs.evaluate(state) >= rhs.evaluate(state);
            case LESS -> lhs.evaluate(state) < rhs.evaluate(state);
            case GREATER -> lhs.evaluate(state) > rhs.evaluate(state);
        } ? 1L : 0L;
    }
}
