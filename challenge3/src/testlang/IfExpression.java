package testlang;

import java.util.Deque;
import java.util.HashMap;

public final class IfExpression extends Expression {
    private final Expression condition, ifValue, elseValue;

    public IfExpression(Expression condition, Expression ifValue, Expression elseValue) {
        this.condition = condition;
        this.ifValue = ifValue;
        this.elseValue = elseValue;
    }

    @Override
    public Long evaluate(Deque<HashMap<String, Long>> state) {
        return condition.evaluate(state).equals(0L) ? elseValue.evaluate(state) : ifValue.evaluate(state);
    }
}
