import barebones.Lexer;
import barebones.Parser;
import barebones.Program;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
            HashMap<String, Long> state = new HashMap<>();
            program.get().execute(state);
            state.forEach((name, value) -> System.out.printf("%s: %s%n", name, value));
        }
    }
}