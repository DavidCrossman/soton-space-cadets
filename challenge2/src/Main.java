import barebones.Lexer;
import barebones.Parser;
import barebones.Program;

import java.util.HashMap;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        String source = """
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

        Optional<Program> program = Parser.parse(Lexer.lex(source));

        if (program.isPresent()) {
            HashMap<String, Long> state = new HashMap<>();
            program.get().execute(state);
            state.forEach((name, value) -> System.out.printf("%s: %s%n", name, value));
        }
    }
}