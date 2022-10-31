package testlang;

import java.util.Deque;
import java.util.HashMap;

public final class Program extends Statement {
    private final Statement statement;
    private final Program rest;

    public Program(Statement statement, Program rest) {
        this.statement = statement;
        this.rest = rest;
    }

    public Program(Statement statement) {
        this(statement, null);
    }

    @Override
    public void execute(Deque<HashMap<String, Long>> state) {
        statement.execute(state);
        if (rest != null) {
            rest.execute(state);
        }
    }
}
