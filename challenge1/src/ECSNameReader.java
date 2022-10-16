import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public final class ECSNameReader {
    private ECSNameReader() {}

    public static void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Email ID: ");

        String userInput;
        try {
            userInput = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!userInput.matches("\\w+")) {
            System.out.println("Invalid input");
            return;
        }

        URL url;
        try {
            url = new URL("https://www.ecs.soton.ac.uk/people/%s".formatted(userInput));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        URLConnection connection;
        try {
            connection = url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Pattern namePattern = Pattern.compile("(?<=(content=\"))[\\w ]+");

        Optional<String> name = Util.getConnectionData(connection).lines()
                .filter(line -> line.contains("og:title"))
                .flatMap(line -> namePattern.matcher(line).results().map(MatchResult::group))
                .filter(Predicate.not(String::isEmpty))
                .findAny();

        System.out.println(name.orElse("Could not find name"));
    }
}
