package barebones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/** Backus-Naur form:
 * <pre>{@literal
 * <identifier> ::= IDENTIFIER
 * <number> ::= NUMBER
 * <value> ::= <identifier> | <number>
 * <comparator> ::= IS | NOT | LESS_EQ | GREATER_EQ | LESS | GREATER
 * <expression-tail> ::= E | <comparator> <value>
 * <expression> ::= <value> <expression-tail>
 * <definition-tail> ::= E | ASSIGN <expression>
 * <definition> ::= VAR <identifier> <definition-tail>
 * <assignment> ::= <identifier> ASSIGN <expression>
 * <scope> ::= OPEN_SCOPE <program> CLOSE_SCOPE
 * <clear> ::= CLEAR <identifier>
 * <increment> ::= INCREMENT <identifier>
 * <decrement> ::= DECREMENT <identifier>
 * <else-tail> ::= <if> | END_STATEMENT <program> END_STATEMENT
 * <if-tail> ::= END END_STATEMENT | ELSE <else-tail>
 * <if> ::= IF <expression> THEN END_STATEMENT <program> <if-tail>
 * <while> ::= WHILE <expression> DO END_STATEMENT <program> END
 * <statement> ::= <definition> | <assignment> | <scope> | <clear> | <increment> | <decrement> | <if> | <while>
 * <program> ::= E | <statement> END_STATEMENT <program>}</pre>
 */

public final class Parser {
    private static final ArrayList<Token.Type> programStartTokens = new ArrayList<>(Arrays.asList(Token.Type.VAR,
            Token.Type.IDENTIFIER, Token.Type.OPEN_SCOPE, Token.Type.CLEAR, Token.Type.INCREMENT, Token.Type.DECREMENT,
            Token.Type.IF, Token.Type.WHILE));
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

    private Number parseNumber() {
        Token number = expectNext(Token.Type.NUMBER);
        long value;
        try {
            value = Long.parseLong(number.getData());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number '%s'".formatted(number.getData()));
        }
        return new Number(value);
    }

    private Identifier parseIdentifier() {
        return new Identifier(expectNext(Token.Type.IDENTIFIER).getData());
    }

    private Expression parseValue() {
        Optional<Token> token = peek();
        if (token.isEmpty()) {
            throw new RuntimeException("Expected value");
        }
        if (token.get().getType() == Token.Type.NUMBER) {
            return parseNumber();
        }
        return parseIdentifier();
    }

    private Expression parseExpressionTail(Expression lhs) {
        Optional<Token> token = peek();

        if (token.isEmpty()) {
            return lhs;
        }

        return switch (token.get().getType()) {
            case IS -> {
                next();
                yield new Comparison(lhs, parseValue(), Comparison.Operator.IS);
            }
            case NOT -> {
                next();
                yield new Comparison(lhs, parseValue(), Comparison.Operator.NOT);
            }
            case LESS_EQ -> {
                next();
                yield new Comparison(lhs, parseValue(), Comparison.Operator.LESS_EQ);
            }
            case GREATER_EQ -> {
                next();
                yield new Comparison(lhs, parseValue(), Comparison.Operator.GREATER_EQ);
            }
            case LESS -> {
                next();
                yield new Comparison(lhs, parseValue(), Comparison.Operator.LESS);
            }
            case GREATER -> {
                next();
                yield new Comparison(lhs, parseValue(), Comparison.Operator.GREATER);
            }
            default -> lhs;
        };
    }

    private Expression parseExpression() {
        return parseExpressionTail(parseValue());
    }

    private Definition parseDefinition() {
        expectNext(Token.Type.VAR);
        Identifier identifier = parseIdentifier();
        Optional<Token> token = peek();
        if (token.isPresent() && token.get().getType() == Token.Type.ASSIGN) {
            next();
            return new Definition(identifier, parseExpression());
        }
        return new Definition(identifier);
    }

    private Assignment parseAssignment() {
        Identifier identifier = parseIdentifier();
        expectNext(Token.Type.ASSIGN);
        return new Assignment(identifier, parseExpression());
    }

    private Scope parseScope() {
        expectNext(Token.Type.OPEN_SCOPE);
        Scope scope = new Scope(parseProgram());
        expectNext(Token.Type.CLOSE_SCOPE);
        return scope;
    }

    private If parseIfTail(Expression condition, Program ifBlock) {
        Optional<Token> token = peek();
        if (token.isPresent() && token.get().getType() == Token.Type.END) {
            next();
            return new If(condition, ifBlock);
        }
        expectNext(Token.Type.ELSE);
        token = peek();
        if (token.isPresent() && token.get().getType() == Token.Type.IF) {
            return new If(condition, ifBlock, new Program(parseIf()));
        }
        expectNext(Token.Type.END_STATEMENT);
        Program elseBlock = parseProgram();
        expectNext(Token.Type.END);
        return new If(condition, ifBlock, elseBlock);
    }

    private If parseIf() {
        expectNext(Token.Type.IF);
        Expression condition = parseExpression();
        expectNext(Token.Type.THEN);
        expectNext(Token.Type.END_STATEMENT);
        return parseIfTail(condition, parseProgram());
    }

    private While parseWhile() {
        expectNext(Token.Type.WHILE);
        Expression condition = parseExpression();
        expectNext(Token.Type.DO);
        expectNext(Token.Type.END_STATEMENT);
        Program block = parseProgram();
        expectNext(Token.Type.END);
        return new While(condition, block);
    }

    private Statement parseStatement() {
        Optional<Token> token = peek();
        if (token.isEmpty()) {
            throw new RuntimeException("Expected statement");
        }
        return switch (token.get().getType()) {
            case VAR -> parseDefinition();
            case IDENTIFIER -> parseAssignment();
            case OPEN_SCOPE -> parseScope();
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
            case IF -> parseIf();
            case WHILE -> parseWhile();
            default -> throw new RuntimeException("Invalid statement");
        };
    }

    private Program parseProgram() {
        Optional<Token> token = peek();
        if (token.isEmpty() || !programStartTokens.contains(token.get().getType())) {
            return null;
        }
        Statement statement = parseStatement();
        expectNext(Token.Type.END_STATEMENT);
        return new Program(statement, parseProgram());
    }

    public static Optional<Program> parse(ArrayList<Token> tokens) {
        Parser parser = new Parser(tokens);
        Optional<Program> program = Optional.ofNullable(parser.parseProgram());
        parser.eof();
        return program;
    }
}
