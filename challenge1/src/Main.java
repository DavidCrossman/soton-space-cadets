import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
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

        Charset charset = Arrays.stream(connection.getContentType().split(";"))
                .map(s -> Charset.forName(s.strip().replace("charset=", ""), null))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(StandardCharsets.US_ASCII);

        String data;
        try {
            InputStream stream = connection.getInputStream();

            data = new String(stream.readAllBytes(), charset);

            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Pattern namePattern = Pattern.compile("(?<=(content=\"))[\\w ]+");

        Optional<String> name = data.lines()
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