package barebones.tree;

public final class Clear extends Tree {
    private final Tree identifier;

    public Clear(Tree identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "Clear %s".formatted(identifier);
    }
}
