package testlang;

import java.util.Deque;
import java.util.HashMap;

public final class Identifier extends Expression {
    private final String name;

    public Identifier(String name) {
        this.name = name;
    }

    public void define(Deque<HashMap<String, Long>> state, Long value) {
        var scope = state.peek();
        if (scope == null) {
            throw new RuntimeException("Current scope does not exist");
        }
        if (scope.containsKey(name)) {
            throw new RuntimeException("Redefinition of variable \"%s\" in the scope".formatted(name));
        }
        scope.put(name, value);
    }

    public void assign(Deque<HashMap<String, Long>> state, Long value) {
        for (var scope : state) {
            if (scope.containsKey(name)) {
                scope.put(name, value);
                return;
            }
        }
        throw new RuntimeException("Variable \"%s\" does not exist".formatted(name));
    }

    @Override
    public Long evaluate(Deque<HashMap<String, Long>> state) {
        for (var scope : state) {
            if (scope.containsKey(name)) {
                Long val = scope.get(name);
                if (val == null) {
                    throw new RuntimeException("Variable \"%s\" is not defined".formatted(name));
                }
                return val;
            }
        }
        throw new RuntimeException("Variable \"%s\" does not exist".formatted(name));
    }
}
