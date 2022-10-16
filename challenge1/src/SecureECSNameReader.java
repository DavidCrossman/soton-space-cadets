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

public class SecureECSNameReader {
    private SecureECSNameReader() {}

    public static void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Authentication cookie (from https://secure.ecs.soton.ac.uk): ");

        String cookie;
        try {
            cookie = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!Util.checkCookie(cookie)) {
            System.out.println("Authentication failed");
            return;
        }

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
            url = new URL("https://secure.ecs.soton.ac.uk/people/%s".formatted(userInput));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        URLConnection connection;
        try {
            connection = url.openConnection();
            connection.setRequestProperty("Cookie", cookie);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Pattern namePattern = Pattern.compile("(?<=(<span itemprop=\"name\">))[\\w ]+");

        Optional<String> name = Util.getConnectionData(connection).lines()
                .flatMap(line -> namePattern.matcher(line.strip().replaceAll("'", "\"")).results()
                        .map(MatchResult::group))
                .filter(Predicate.not(String::isEmpty))
                .findAny();

        System.out.println(name.orElse("Could not find name"));
    }
}
