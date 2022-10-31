package testlang;

public final class Token {
    public enum Type {
        END_STATEMENT,
        OPEN_BRACKET,
        CLOSE_BRACKET,
        OPEN_BRACE,
        CLOSE_BRACE,
        AND,
        OR,
        EQUALS,
        NOT_EQ,
        LESS_EQ,
        GREATER_EQ,
        LESS,
        GREATER,
        DIVIDE,
        MULTIPLY,
        MINUS,
        PLUS,
        VAR,
        ASSIGN,
        IF,
        ELSE,
        WHILE,
        IDENTIFIER,
        NUMBER;

        String pattern() {
            return switch (this) {
                case END_STATEMENT -> ";";
                case OPEN_BRACKET -> "\\(";
                case CLOSE_BRACKET -> "\\)";
                case OPEN_BRACE -> "\\{";
                case CLOSE_BRACE -> "\\}";
                case AND -> "\\&\\&";
                case OR -> "\\|\\|";
                case EQUALS -> "==";
                case NOT_EQ -> "!=";
                case LESS_EQ -> "\\<=";
                case GREATER_EQ -> "\\>=";
                case LESS -> "\\<";
                case GREATER -> "\\>";
                case DIVIDE -> "\\/";
                case MULTIPLY -> "\\*";
                case MINUS -> "\\-";
                case PLUS -> "\\+";
                case VAR -> "var\\b";
                case ASSIGN -> "=";
                case IF -> "if\\b";
                case ELSE -> "else\\b";
                case WHILE -> "while\\b";
                case IDENTIFIER -> "[a-zA-Z]\\w*";
                case NUMBER -> "\\d++(?=\\s|;|\\*|/|\\+|-|!|=|<|>|&|\\||\\{|}|\\(|\\)|$)";
            };
        }

        String text() {
            return switch (this) {
                case END_STATEMENT -> "';'";
                case OPEN_BRACKET -> "'('";
                case CLOSE_BRACKET -> "')'";
                case OPEN_BRACE -> "'{'";
                case CLOSE_BRACE -> "'}'";
                case AND -> "'&&'";
                case OR -> "'||'";
                case EQUALS -> "'=='";
                case NOT_EQ -> "'!='";
                case LESS_EQ -> "'<='";
                case GREATER_EQ -> "'>='";
                case LESS -> "'<'";
                case GREATER -> "'>'";
                case DIVIDE -> "'/'";
                case MULTIPLY -> "'*'";
                case MINUS -> "'-'";
                case PLUS -> "'+'";
                case VAR -> "'var'";
                case ASSIGN -> "'='";
                case IF -> "'if'";
                case ELSE -> "'else'";
                case WHILE -> "'while'";
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

    @SuppressWarnings("unused")
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
