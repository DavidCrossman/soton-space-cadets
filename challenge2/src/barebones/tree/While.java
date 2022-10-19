package barebones.tree;

public final class While extends Tree {
    private final Tree condition, block;

    public While(Tree condition, Tree block) {
        this.condition = condition;
        this.block = block;
    }

    @Override
    public String toString() {
        return "While %s do {\n%s\n} end".formatted(condition, block == null ? "" : block);
    }
}
