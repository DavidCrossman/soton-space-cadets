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

public final class ECSNameReader {
    private ECSNameReader() {}

    public static void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Email ID: ");

        String spec;
        try {
            spec = "https://www.ecs.soton.ac.uk/people/%s".formatted(reader.readLine());
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Pattern namePattern = Pattern.compile("(?<=(content=\"))[\\w ]+");

        Optional<String> name = Util.getConnectionData(connection).lines()
                .map(String::strip)
                .filter(line -> line.startsWith("<meta property=\"og:title\""))
                .map(line -> {
                    Matcher m = namePattern.matcher(line);
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
