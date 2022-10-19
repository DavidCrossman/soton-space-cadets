package barebones.tree;

public final class Increment extends Tree {
    private final Tree identifier;

    public Increment(Tree identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "Increment %s".formatted(identifier);
    }
}
