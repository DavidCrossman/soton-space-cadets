import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Anagram {
    private Anagram() {}

    public static void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Text: ");

        String userInput;
        try {
            userInput = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!userInput.matches("[a-zA-Z ]+")) {
            System.out.println("Invalid input");
            return;
        }

        URL url;
        try {
            url = new URL("https://new.wordsmith.org/anagram/anagram.cgi?anagram=%s"
                    .formatted(URLEncoder.encode(userInput, StandardCharsets.UTF_8)));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        URLConnection connection;
        try {
            connection = url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Pattern pattern = Pattern.compile("^([a-zA-Z ]+)<br>");

       Set<String> anagrams = Util.getConnectionData(connection).lines()
                .flatMap(line -> pattern.matcher(line).results()
                        .filter(result -> result.groupCount() > 0 && !result.group(1).equalsIgnoreCase(userInput))
                        .map(result -> result.group(1)))
               .limit(200)
               .collect(Collectors.toSet());

       anagrams.forEach(System.out::println);
    }
}
