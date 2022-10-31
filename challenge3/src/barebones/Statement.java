package barebones;

import java.util.Deque;
import java.util.HashMap;

public abstract class Statement {
    public abstract void execute(Deque<HashMap<String, Long>> state);
}
