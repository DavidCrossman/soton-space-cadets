package barebones.tree;

public final class Decrement extends Tree {
    private final Tree identifier;

    public Decrement(Tree identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "Decrement %s".formatted(identifier);
    }
}
