package barebones.tree;

import java.util.HashMap;

public abstract class Expression extends Tree {
    public abstract Long evaluate(HashMap<String, Long> state);
}
