package barebones;

public class Token {
    public enum Type {
        END_STATEMENT,
        NOT,
        VAR,
        ASSIGN,
        CLEAR,
        INCREMENT,
        DECREMENT,
        WHILE,
        DO,
        END,
        IDENTIFIER,
        NUMBER;

        String pattern() {
            return switch (this) {
                case END_STATEMENT -> ";";
                case NOT -> "not\\b";
                case VAR -> "var\\b";
                case ASSIGN -> "=";
                case CLEAR -> "clear\\b";
                case INCREMENT -> "incr\\b";
                case DECREMENT -> "decr\\b";
                case WHILE -> "while\\b";
                case DO -> "do\\b";
                case END -> "end\\b";
                case IDENTIFIER -> "[a-zA-Z]\\w*";
                case NUMBER -> "\\d++(?=\\s|;|$)";
            };
        }

        String text() {
            return switch (this) {
                case END_STATEMENT -> "';'";
                case NOT -> "'not'";
                case VAR -> "'var'";
                case ASSIGN -> "'='";
                case CLEAR -> "'clear'";
                case INCREMENT -> "'incr'";
                case DECREMENT -> "'decr'";
                case WHILE -> "'while'";
                case DO -> "'do'";
                case END -> "'end'";
                case IDENTIFIER -> "identifier";
                case NUMBER -> "number";
            };
        }
    }

    private final Type type;
    private final String data;

    public Token(Type type, String data) {
        this.type = type;
        if (hasData()) {
            this.data = data;
        } else {
            this.data = null;
        }
    }

    public Token(Type type) {
        this(type, null);
    }

    public boolean hasData() {
        return type == Type.IDENTIFIER || type == Type.NUMBER;
    }

    public Type getType() {
        return type;
    }

    public String getData() {
        return data;
    }
}
