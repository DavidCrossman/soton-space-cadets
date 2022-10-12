import java.io.IOException;
import java.io.InputStream;
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
}
