package barebones.tree;

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
}
