import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public final class Util {
    private Util() {}

    public static Charset getConnectionCharset(URLConnection connection) {
        return Arrays.stream(connection.getContentType().split(";"))
                .map(s -> Charset.forName(s.strip().replace("charset=", ""), null))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(StandardCharsets.US_ASCII);
    }

    public static String getConnectionData(URLConnection connection) {
        String data;
        try {
            InputStream stream = connection.getInputStream();

            data = new String(stream.readAllBytes(), getConnectionCharset(connection));

            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    /**
     * Checks if the given cookie authorises access to the domain <a href="https://secure.ecs.soton.ac.uk">https://secure.ecs.soton.ac.uk</a>
     * @param cookie The cookie, formatted "name=value"
     * @return Whether the given cookie succeeded authorisation
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean checkCookie(String cookie) {
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
}
