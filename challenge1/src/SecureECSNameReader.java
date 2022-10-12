import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecureECSNameReader {
    private SecureECSNameReader() {}

    private static boolean checkCookie(String cookie) {
        URL url;
        try {
            url = new URL("https://secure.ecs.soton.ac.uk");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        URLConnection connection;
        try {
            connection = url.openConnection();
            connection.setRequestProperty("Cookie", cookie);
            connection.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "Apache".equals(connection.getHeaderField("Server"));
    }

    public static void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Authentication cookie (from https://secure.ecs.soton.ac.uk): ");

        String cookie;
        try {
            cookie = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!checkCookie(cookie)) {
            System.out.println("Authentication failed");
            return;
        }

        System.out.print("Email ID: ");

        String spec;
        try {
            spec = "https://secure.ecs.soton.ac.uk/people/%s".formatted(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        URL url;
        try {
            url = new URL(spec);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        URLConnection connection;
        try {
            connection = url.openConnection();
            connection.setRequestProperty("Cookie", cookie);
            //connection.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Pattern namePattern = Pattern.compile("(?<=(<span itemprop=\"name\">))[\\w ]+");

        Optional<String> name = Util.getConnectionData(connection).lines()
                .map(line -> {
                    Matcher m = namePattern.matcher(line.strip().replaceAll("'", "\""));
                    return m.find() ? m.group() : null;
                })
                .filter(Objects::nonNull)
                .findAny();

        if (name.isPresent()) {
            System.out.println(name.get());
        } else {
            System.out.println("Could not find name");
        }
    }
}
