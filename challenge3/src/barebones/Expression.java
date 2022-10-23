package barebones;

import java.util.Deque;
import java.util.HashMap;

public abstract class Expression {
    public abstract Long evaluate(Deque<HashMap<String, Long>> state);
}
