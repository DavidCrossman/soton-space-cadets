package barebones;

import java.util.HashMap;

public final class Program extends Statement {
    private final Statement statement;
    private final Program rest;

    public Program(Statement statement, Program rest) {
        this.statement = statement;
        this.rest = rest;
    }

    @Override
    public String toString() {
        return rest == null ? "[%s];".formatted(statement) : "[%s];\n%s".formatted(statement, rest);
    }

    @Override
    public void execute(HashMap<String, Long> state) {
        statement.execute(state);
        state.forEach((name, value) -> System.out.printf("%s: %s%n", name, value));
        System.out.println();
        if (rest != null) {
            rest.execute(state);
        }
    }
}
