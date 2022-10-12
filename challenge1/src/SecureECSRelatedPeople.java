import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class SecureECSRelatedPeople {
    private SecureECSRelatedPeople() {}

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
            url = new URL("https://secure.ecs.soton.ac.uk/people/%s/related_people".formatted(userInput));
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

        final Pattern namePattern = Pattern.compile("<a href=\"https://secure\\.ecs\\.soton\\.ac\\.uk/people/(\\w+)\">([\\w ]+)");

        Set<String> relatedPeople = Util.getConnectionData(connection).lines()
                .flatMap(line -> namePattern.matcher(line.replaceAll("'", "\"")).results()
                        .filter(result -> result.groupCount() > 0 && !userInput.equals(result.group(1)))
                        .map(result -> "%s (%s)".formatted(result.group(2), result.group(1))))
                .collect(Collectors.toSet());

        relatedPeople.forEach(System.out::println);
    }
}
