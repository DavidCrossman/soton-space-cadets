import testlang.Lexer;
import testlang.Parser;
import testlang.Program;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) return;

        String source;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            source = reader.lines().collect(Collectors.joining("\n"));
            reader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File \"%s\" not found%n".formatted(args[0]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Optional<Program> program = Parser.parse(Lexer.lex(source));

        if (program.isPresent()) {
            Deque<HashMap<String, Long>> state = new ArrayDeque<>() {{
                push(new HashMap<>());
            }};
            program.get().execute(state);
            var scope = state.peek();
            if (scope != null) scope.forEach((name, value) -> System.out.printf("%s: %s%n", name, value));
        }
    }
}