package barebones.tree;

import java.util.HashMap;

public final class EndStatement extends Tree {
    private final Tree statement, rest;

    public EndStatement(Tree statement, Tree rest) {
        this.statement = statement;
        this.rest = rest;
    }

    @Override
    public String toString() {
        return rest == null ? "[%s];".formatted(statement) : "[%s];\n%s".formatted(statement, rest);
    }

    public Tree getStatement() {
        return statement;
    }

    public Tree getRest() {
        return rest;
    }

    @Override
    public void execute(HashMap<String, Long> state) {
        statement.execute(state);
        if (rest != null) {
            rest.execute(state);
        }
    }
}
