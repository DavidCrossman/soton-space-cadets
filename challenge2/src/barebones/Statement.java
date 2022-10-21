package barebones;

import java.util.HashMap;

public abstract class Statement {
    public abstract void execute(HashMap<String, Long> state);
}
