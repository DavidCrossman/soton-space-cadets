package barebones.tree;

public final class Number extends Tree {
    private final Long value;

    public Number(Long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "'%s'".formatted(value);
    }
}
