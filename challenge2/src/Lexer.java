import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Lexer {
    private static final Pattern pattern = Pattern.compile(Arrays.stream(Token.Type.values())
            .map(Token.Type::pattern)
            .collect(Collectors.joining(")|(", "(", ")")) + "|[^\\s;]++");

    private Lexer() {}

    public static ArrayList<Token> lex(String line) {
        return pattern.matcher(line).results().map(result -> {
                    for (Token.Type type : Token.Type.values()) {
                        String group = result.group(type.ordinal() + 1);
                        if (group != null) return new Token(type, group);
                    }
                    throw new RuntimeException("Error token: %s".formatted(result.group()));
                }).collect(Collectors.toCollection(ArrayList::new));
    }

    public static ArrayList<Token> lexOld(String line) {
        ArrayList<Token> tokens = new ArrayList<>();

        char[] chars = line.toCharArray();
        char c;
        for (int i = 0; i < chars.length; ++i) {
            c = chars[i];

            if (Character.isDigit(c)) {
                StringBuilder number = new StringBuilder();
                int j;
                for (j = i; j < chars.length; ++j) {
                    if (Character.isDigit(chars[j])) {
                        number.append(chars[j]);
                    } else if (chars[j] == ';' || Character.isWhitespace(chars[j])) {
                        --j;
                        break;
                    } else {
                        throw new RuntimeException("Invalid character %s in number at index %s"
                                .formatted(chars[j], j));
                    }
                }
                tokens.add(new Token(Token.Type.NUMBER, number.toString()));
                i = j;
            } else if (Character.isAlphabetic(c)) {
                StringBuilder word = new StringBuilder();
                int j;
                for (j = i; j < chars.length; ++j) {
                    if (Character.isLetterOrDigit(chars[j]) || chars[j] == '_') {
                        word.append(chars[j]);
                    } else if (chars[j] == ';' || Character.isWhitespace(chars[j])) {
                        --j;
                        break;
                    } else {
                        throw new RuntimeException("Invalid character %s in identifier at index %s"
                                .formatted(chars[j], j));
                    }
                }
                Token.Type type = switch (word.toString()) {
                    case "clear" -> Token.Type.CLEAR;
                    case "incr" -> Token.Type.INCREMENT;
                    case "decr" -> Token.Type.DECREMENT;
                    case "not" -> Token.Type.NOT;
                    case "while" -> Token.Type.WHILE;
                    case "do" -> Token.Type.DO;
                    case "end" -> Token.Type.END;
                    default -> Token.Type.IDENTIFIER;
                };
                tokens.add(type == Token.Type.IDENTIFIER ?
                        new Token(type, word.toString()) :
                        new Token(type));
                i = j;
            } else if (c == ';') {
                tokens.add(new Token(Token.Type.END_STATEMENT));
            } else if (!Character.isWhitespace(c)) {
                throw new RuntimeException("Invalid character %s at index %s".formatted(c, i));
            }
        }
        return tokens;
    }
}
