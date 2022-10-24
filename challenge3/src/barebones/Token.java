package barebones;

public class Token {
    public enum Type {
        END_STATEMENT,
        OPEN_SCOPE,
        CLOSE_SCOPE,
        IS,
        NOT,
        LESS_EQ,
        GREATER_EQ,
        LESS,
        GREATER,
        VAR,
        ASSIGN,
        CLEAR,
        INCREMENT,
        DECREMENT,
        IF,
        THEN,
        ELSE,
        WHILE,
        DO,
        END,
        IDENTIFIER,
        NUMBER;

        String pattern() {
            return switch (this) {
                case END_STATEMENT -> ";";
                case OPEN_SCOPE -> "\\{";
                case CLOSE_SCOPE -> "\\}";
                case IS -> "==|is\\b";
                case NOT -> "!=|not\\b";
                case LESS_EQ -> "<=";
                case GREATER_EQ -> ">=";
                case LESS -> "<";
                case GREATER -> ">";
                case VAR -> "var\\b";
                case ASSIGN -> "=";
                case CLEAR -> "clear\\b";
                case INCREMENT -> "incr\\b";
                case DECREMENT -> "decr\\b";
                case IF -> "if\\b";
                case THEN -> "then\\b";
                case ELSE -> "else\\b";
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
                case OPEN_SCOPE -> "'{'";
                case CLOSE_SCOPE -> "'}'";
                case IS -> "'=='";
                case NOT -> "'!='";
                case LESS_EQ -> "'<='";
                case GREATER_EQ -> "'>='";
                case LESS -> "'<'";
                case GREATER -> "'>'";
                case VAR -> "'var'";
                case ASSIGN -> "'='";
                case CLEAR -> "'clear'";
                case INCREMENT -> "'incr'";
                case DECREMENT -> "'decr'";
                case IF -> "'if'";
                case THEN -> "'then'";
                case ELSE -> "'else'";
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
