package barebones.tree;

public final class Identifier extends Tree {
    private final String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "\"%s\"".formatted(name);
    }
}
