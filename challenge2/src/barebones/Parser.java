package barebones;

import barebones.tree.Number;
import barebones.tree.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public final class Parser {
    private static final ArrayList<Token.Type> programStartTokens = new ArrayList<>(Arrays.asList(Token.Type.CLEAR,
            Token.Type.INCREMENT, Token.Type.DECREMENT, Token.Type.WHILE));
    private final ArrayList<Token> tokens;
    private int index;

    private Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        index = 0;
    }

    private Optional<Token> next() {
        if (index >= tokens.size()) {
            return Optional.empty();
        }
        return Optional.of(tokens.get(index++));
    }

    private Optional<Token> peek() {
        if (index >= tokens.size()) {
            return Optional.empty();
        }
        return Optional.of(tokens.get(index));
    }

    private Token expectNext(Token.Type expectedType) {
        Optional<Token> token = next();
        if (token.isEmpty()) {
            throw new RuntimeException("Expected %s".formatted(expectedType.text()));
        }
        if (token.get().getType() != expectedType) {
            String message = "Expected %s, found %s".formatted(expectedType.text(), token.get().getType().text());
            if (token.get().hasData()) {
                message += " \"%s\"".formatted(token.get().getData());
            }
            throw new RuntimeException(message);
        }
        return token.get();
    }

    private void eof() {
        Optional<Token> token = peek();
        if (token.isPresent()) {
            String message = "Expected EOF, found %s".formatted(token.get().getType().text());
            if (token.get().hasData()) {
                message += " \"%s\"".formatted(token.get().getData());
            }
            throw new RuntimeException(message);
        }
    }

    private Tree parseNumber() {
        Token number = expectNext(Token.Type.NUMBER);
        long value;
        try {
            value = Long.parseLong(number.getData());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number");
        }
        return new Number(value);
    }

    private Tree parseIdentifier() {
        return new Identifier(expectNext(Token.Type.IDENTIFIER).getData());
    }

    private Tree parseValue() {
        Optional<Token> token = peek();
        if (token.isEmpty()) {
            throw new RuntimeException("Expected value");
        }
        if (token.get().getType() == Token.Type.NUMBER) {
            return parseNumber();
        }
        return parseIdentifier();
    }

    private Tree parseExpressionTail(Tree lhs) {
        Optional<Token> token = peek();

        if (token.isEmpty()) {
            return lhs;
        }

        return switch (token.get().getType()) {
            case NOT -> {
                next();
                yield new Not(lhs, parseValue());
            }
            default -> lhs;
        };
    }

    private Tree parseExpression() {
        return parseExpressionTail(parseValue());
    }

    private Tree parseWhile() {
        expectNext(Token.Type.WHILE);
        Tree condition = parseExpression();
        expectNext(Token.Type.DO);
        expectNext(Token.Type.END_STATEMENT);
        Tree block = parseProgram();
        expectNext(Token.Type.END);
        return new While(condition, block);
    }

    private Tree parseStatement() {
        Optional<Token> token = peek();
        if (token.isEmpty()) {
            throw new RuntimeException("Expected statement");
        }
        return switch (token.get().getType()) {
            case CLEAR -> {
                next();
                yield new Clear(parseIdentifier());
            }
            case INCREMENT -> {
                next();
                yield new Increment(parseIdentifier());
            }
            case DECREMENT -> {
                next();
                yield new Decrement(parseIdentifier());
            }
            case WHILE -> parseWhile();
            default -> throw new RuntimeException("Invalid statement");
        };
    }

    private Tree parseProgram() {
        Optional<Token> token = peek();
        if (token.isEmpty() || !programStartTokens.contains(token.get().getType())) {
            return null;
        }
        Tree statement = parseStatement();
        expectNext(Token.Type.END_STATEMENT);
        return new EndStatement(statement, parseProgram());
    }

    public static Optional<Tree> parse(ArrayList<Token> tokens) {
        Parser parser = new Parser(tokens);
        Optional<Tree> program = Optional.ofNullable(parser.parseProgram());
        parser.eof();
        return program;
    }
}
