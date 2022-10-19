package barebones.tree;

public final class Not extends Tree {
    private final Tree lhs, rhs;

    public Not(Tree lhs, Tree rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String toString() {
        return "(%s not %s)".formatted(lhs, rhs);
    }
}
