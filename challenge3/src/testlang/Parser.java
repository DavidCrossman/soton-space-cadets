package testlang;

import java.util.*;

/** Backus-Naur form:
 *<pre>{@literal
 *<number> ::= NUMBER
 *<identifier> ::= IDENTIFIER
 *<term> ::= <number> | <identifier> | MINUS <term> | OPEN_BRACKET <expression> CLOSE_BRACKET
 *<multTail> ::= E | (MULTIPLY | DIVIDE) <term> <multTail>
 *<multTerm> ::= <term> <multTail>
 *<addTail> ::= E | (PLUS | MINUS) <multTerm> <addTail>
 *<addTerm> ::= <multTerm> <addTail>
 *<relationTail> ::= E | (EQUALS | NOT_EQ | LESS_EQ | GREATER_EQ | LESS | GREATER) <addTerm> <relationTail>
 *<relationTerm> ::= <addTerm> <relationTail>
 *<andTail> ::= E | AND <relationTerm> <andTail>
 *<andTerm> ::= <relationTerm> <andTail>
 *<orTail> ::= E | OR <relationTerm> <orTail>
 *<orTerm> ::= <relationTerm> <orTail>
 *<ifExprTail> ::= <ifExpr> | OPEN_BRACE <expression> CLOSE_BRACE
 *<ifExpr> ::= IF <expression> OPEN_BRACE <expression> CLOSE_BRACE ELSE <ifExprTail>
 *<expression> ::= <ifExpr> | <orTerm>
 *<definitionTail> ::= END_STATEMENT | ASSIGN <expression> END_STATEMENT
 *<definition> ::= VAR <identifier> <definitionTail>
 *<assignment> ::= <identifier> ASSIGN <expression> END_STATEMENT
 *<block> ::= OPEN_BRACE <program> CLOSE_BRACE
 *<ifTail> ::= E | ELSE (<if> | <block>)
 *<if>::= IF <expression> <block> <ifTail>
 *<while>::= WHILE <expression> <block>
 *<statement> ::= <definition> | <assignment> | <block> | <if> | <while>
 *<program> ::= E | <statement> <program>}</pre>
 */

public final class Parser {
    private static final ArrayList<Token.Type> programStartTokens = new ArrayList<>(Arrays.asList(Token.Type.VAR,
            Token.Type.IDENTIFIER, Token.Type.OPEN_BRACE, Token.Type.IF, Token.Type.WHILE));
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

    private Expression parseBrackets() {
        expectNext(Token.Type.OPEN_BRACKET);
        Expression expression = parseExpression();
        expectNext(Token.Type.CLOSE_BRACKET);
        return expression;
    }

    private Expression parseTerm() {
        Optional<Token> token = peek();
        if (token.isEmpty()) {
            throw new RuntimeException("Expected term");
        }

        return switch (token.get().getType()) {
            case NUMBER -> parseNumber();
            case IDENTIFIER -> parseIdentifier();
            case OPEN_BRACKET -> parseBrackets();
            case MINUS -> {
                next();
                yield new UnaryOperator(parseTerm(), term -> -term);
            }
            default -> {
                String message = "Expected term, found %s".formatted(token.get().getType().text());
                if (token.get().hasData()) {
                    message += " \"%s\"".formatted(token.get().getData());
                }
                throw new RuntimeException(message);
            }
        };
    }

    private Expression parseMultTail(Expression lhs) {
        Optional<Token> token = peek();
        if (token.isEmpty()) {
            return lhs;
        }
        if (token.get().getType() == Token.Type.MULTIPLY) {
            next();
            return parseMultTail(new BinaryOperator(lhs, parseTerm(),
                    (multiplicand, multiplier) -> multiplicand * multiplier));
        }
        if (token.get().getType() == Token.Type.DIVIDE) {
            next();
            return parseMultTail(new BinaryOperator(lhs, parseTerm(),
                    (dividend, divisor) -> {
                        if (divisor == 0) throw new RuntimeException("Division by zero");
                        return dividend / divisor;
                    }));
        }
        return lhs;
    }

    private Expression parseMultTerm() {
        return parseMultTail(parseTerm());
    }

    private Expression parseAddTail(Expression lhs) {
        Optional<Token> token = peek();
        if (token.isEmpty()) {
            return lhs;
        }
        if (token.get().getType() == Token.Type.PLUS) {
            next();
            return parseAddTail(new BinaryOperator(lhs, parseMultTerm(), Long::sum));
        }
        if (token.get().getType() == Token.Type.MINUS) {
            next();
            return parseAddTail(new BinaryOperator(lhs, parseMultTerm(),
                    (minuend, subtrahend) -> minuend - subtrahend));
        }
        return lhs;
    }

    private Expression parseAddTerm() {
        return parseAddTail(parseMultTerm());
    }

    private Expression parseRelationTail(Expression lhs) {
        Optional<Token> token = peek();
        if (token.isEmpty()) {
            return lhs;
        }
        Token.Type type = token.get().getType();
        if (type == Token.Type.EQUALS || type == Token.Type.NOT_EQ || type == Token.Type.LESS_EQ ||
                type == Token.Type.GREATER_EQ || type == Token.Type.LESS || type == Token.Type.GREATER) {
            next();
            java.util.function.BinaryOperator<Long> operator = switch (type) {
                case EQUALS -> (left, right) -> left.equals(right) ? 1L : 0L;
                case NOT_EQ -> (left, right) -> !left.equals(right) ? 1L : 0L;
                case LESS_EQ -> (left, right) -> left <= right ? 1L : 0L;
                case GREATER_EQ -> (left, right) -> left >= right ? 1L : 0L;
                case LESS -> (left, right) -> left < right ? 1L : 0L;
                case GREATER -> (left, right) -> left > right ? 1L : 0L;
                default -> throw new RuntimeException("Unexpected relation operator %s".formatted(type));
            };
            return parseRelationTail(new BinaryOperator(lhs, parseAddTerm(), operator));
        }
        /*if (token.get().getType() == Token.Type.EQUALS) {
            next();
            return parseRelationTail(new BinaryOperator(lhs, parseTerm(),
                    (left, right) -> left.equals(right) ? 1L : 0L ));
        }
        if (token.get().getType() == Token.Type.NOT_EQ) {
            next();
            return parseRelationTail(new BinaryOperator(lhs, parseTerm(),
                    (left, right) -> !left.equals(right) ? 1L : 0L ));
        }
        if (token.get().getType() == Token.Type.LESS_EQ) {
            next();
            return parseRelationTail(new BinaryOperator(lhs, parseTerm(),
                    (left, right) -> left <= right ? 1L : 0L ));
        }
        if (token.get().getType() == Token.Type.GREATER_EQ) {
            next();
            return parseRelationTail(new BinaryOperator(lhs, parseTerm(),
                    (left, right) -> left >= right ? 1L : 0L ));
        }
        if (token.get().getType() == Token.Type.LESS) {
            next();
            return parseRelationTail(new BinaryOperator(lhs, parseTerm(),
                    (left, right) -> left < right ? 1L : 0L ));
        }
        if (token.get().getType() == Token.Type.GREATER) {
            next();
            return parseRelationTail(new BinaryOperator(lhs, parseTerm(),
                    (left, right) -> left > right ? 1L : 0L ));
        }*/
        return lhs;
    }

    private Expression parseRelationTerm() {
        return parseRelationTail(parseAddTerm());
    }

    private Expression parseAndTail(Expression lhs) {
        Optional<Token> token = peek();
        if (token.isEmpty()) {
            return lhs;
        }
        if (token.get().getType() == Token.Type.AND) {
            next();
            return parseAddTail(new BinaryOperator(lhs, parseRelationTerm(),
                    (left, right) -> left != 0 && right != 0 ? 1L : 0L));
        }
        return lhs;
    }

    private Expression parseAndTerm() {
        return parseAndTail(parseRelationTerm());
    }

    private Expression parseOrTail(Expression lhs) {
        Optional<Token> token = peek();
        if (token.isEmpty()) {
            return lhs;
        }
        if (token.get().getType() == Token.Type.OR) {
            next();
            return parseOrTail(new BinaryOperator(lhs, parseAndTerm(),
                    (left, right) -> left != 0 || right != 0 ? 1L : 0L));
        }
        return lhs;
    }

    private Expression parseOrTerm() {
        return parseOrTail(parseAndTerm());
    }

    private Expression parseExpression() {
        return parseOrTerm();
    }

    private Definition parseDefinition() {
        expectNext(Token.Type.VAR);
        Identifier identifier = parseIdentifier();
        Optional<Token> token = peek();
        if (token.isPresent() && token.get().getType() == Token.Type.ASSIGN) {
            next();
            Expression expression = parseExpression();
            expectNext(Token.Type.END_STATEMENT);
            return new Definition(identifier, expression);
        }
        expectNext(Token.Type.END_STATEMENT);
        return new Definition(identifier);
    }

    private Assignment parseAssignment() {
        Identifier identifier = parseIdentifier();
        expectNext(Token.Type.ASSIGN);
        Expression expression = parseExpression();
        expectNext(Token.Type.END_STATEMENT);
        return new Assignment(identifier, expression);
    }

    private Block parseBlock() {
        expectNext(Token.Type.OPEN_BRACE);
        Block block = new Block(parseProgram());
        expectNext(Token.Type.CLOSE_BRACE);
        return block;
    }

    private IfStatement parseIfTail(Expression condition, Block ifBlock) {
        Optional<Token> token = peek();
        if (token.isEmpty() || token.get().getType() != Token.Type.ELSE) {
            return new IfStatement(condition, ifBlock);
        }
        expectNext(Token.Type.ELSE);
        token = peek();
        if (token.isPresent() && token.get().getType() == Token.Type.IF) {
            return new IfStatement(condition, ifBlock, new Block(new Program(parseIf()), false));
        }
        return new IfStatement(condition, ifBlock, parseBlock());
    }

    private IfStatement parseIf() {
        expectNext(Token.Type.IF);
        return parseIfTail(parseExpression(), parseBlock());
    }

    private While parseWhile() {
        expectNext(Token.Type.WHILE);
        return new While(parseExpression(), parseBlock());
    }

    private Statement parseStatement() {
        Optional<Token> token = peek();
        if (token.isEmpty()) {
            throw new RuntimeException("Expected statement");
        }
        return switch (token.get().getType()) {
            case VAR -> parseDefinition();
            case IDENTIFIER -> parseAssignment();
            case OPEN_BRACE -> parseBlock();
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
        return new Program(parseStatement(), parseProgram());
    }

    public static Optional<Program> parse(ArrayList<Token> tokens) {
        Parser parser = new Parser(tokens);
        Optional<Program> program = Optional.ofNullable(parser.parseProgram());
        parser.eof();
        return program;
    }
}
