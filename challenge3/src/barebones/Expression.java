package barebones;

import java.util.HashMap;

public abstract class Expression {
    public abstract Long evaluate(HashMap<String, Long> state);
}
