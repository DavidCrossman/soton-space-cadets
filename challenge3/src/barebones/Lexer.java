package barebones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Lexer {
    private static final Pattern pattern = Pattern.compile(Arrays.stream(Token.Type.values())
            .map(Token.Type::pattern)
            .collect(Collectors.joining(")|(", "(", ")")) + "|[^\\s;]++");

    private Lexer() {}

    public static ArrayList<Token> lex(String text) {
        return pattern.matcher(text).results().map(result -> {
                    for (Token.Type type : Token.Type.values()) {
                        String group = result.group(type.ordinal() + 1);
                        if (group != null) return new Token(type, group);
                    }
                    throw new RuntimeException("Error token: %s".formatted(result.group()));
                }).collect(Collectors.toCollection(ArrayList::new));
    }
}
