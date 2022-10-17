public class Main {
    public static void main(String[] args) {
        String program = """
                clear X;
                incr X;
                incr X;
                clear Y;
                incr Y;
                incr Y;
                incr Y;
                clear Z;
                while X not 0 do;
                   clear W;
                   while Y not 0 do;
                      incr Z;
                      incr W;
                      decr Y;
                   end;
                   while W not 0 do;
                      incr Y;
                      decr W;
                   end;
                   decr X;
                end;""";

        program.lines().flatMap(line -> Lexer.lex(line).stream())
                .forEach(token -> System.out.println(token.getData() == null ?
                        "[%s]".formatted(token.getType().toString()) :
                        "[%s, %s]".formatted(token.getType().toString(), token.getData())));
    }
}