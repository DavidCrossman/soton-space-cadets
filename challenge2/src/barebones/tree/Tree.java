package barebones.tree;

import java.util.HashMap;

/** Backus-Naur form:
 * <pre>{@literal
 * <identifier> ::= IDENTIFIER
 * <number> ::= NUMBER
 * <value> ::= <identifier> | <number>
 * <comparator> ::= NOT
 * <expression-tail> ::= E | <comparator> <value>
 * <expression> ::= <value> <expression-tail>
 * <clear> ::= CLEAR <identifier>
 * <increment> ::= INCREMENT <identifier>
 * <decrement> ::= DECREMENT <identifier>
 * <while> ::= WHILE <expression> DO END_STATEMENT <program> END
 * <statement> ::= <clear> | <increment> | <decrement> | <while>
 * <program> ::= E | <statement> END_STATEMENT <program>}</pre>
 */

public abstract class Tree {
    public abstract void execute(HashMap<String, Long> state);
}
