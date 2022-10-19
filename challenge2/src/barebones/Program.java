package barebones;

import barebones.tree.*;
import barebones.tree.Number;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

public class Program {
    private final HashMap<String, Long> variables;
    private final Deque<Tree> stack;

    public Program(Tree program) {
        stack = new ArrayDeque<>();
        stack.push(program);
        variables = new HashMap<>();
    }

    public void run() {
        while (!stack.isEmpty()) {
            Tree command = stack.pop();
            if (command instanceof Clear clr) {
                variables.put(clr.getIdentifier().getName(), 0L);
            } else if (command instanceof Increment inc) {
                variables.put(inc.getIdentifier().getName(), variables.get(inc.getIdentifier().getName()) + 1);
            } else if (command instanceof Decrement dec) {
                variables.put(dec.getIdentifier().getName(), variables.get(dec.getIdentifier().getName()) - 1);
            } else if (command instanceof EndStatement end) {
                if (end.getRest() != null) stack.push(end.getRest());
                stack.push(end.getStatement());
            } else if (command instanceof While whl) {
                if (whl.getCondition() instanceof Not not) {
                    Long lhs = 0L, rhs = 0L;
                    if (not.getLhs() instanceof Identifier id) {
                        lhs = variables.get(id.getName());
                    } else if (not.getLhs() instanceof Number num) {
                        lhs = num.getValue();
                    }
                    if (not.getRhs() instanceof Identifier id) {
                        rhs = variables.get(id.getName());
                    } else if (not.getRhs() instanceof Number num) {
                        rhs = num.getValue();
                    }
                    if (!lhs.equals(rhs)) {
                        stack.push(whl);
                        if (whl.getBlock() != null) stack.push(whl.getBlock());
                    }
                }
            }
        }

        variables.forEach((name, value) -> System.out.printf("%s: %s%n", name, value));
    }
}
