package client;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        int port = 8263;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try (Socket socket = new Socket("localhost", port)) {
            System.out.printf("Connected to the server using port %s%nUsername: ", port);
            String username = reader.readLine();

            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeByte(2);
            outputStream.writeUTF("User \"%s\" has connected to the server".formatted(username));
            outputStream.flush();

            Thread serverHandler = new Thread(() -> {
                DataInputStream inputStream;
                try {
                    inputStream = new DataInputStream(socket.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                while (true) {
                    try {
                        System.out.println(inputStream.readUTF());
                    } catch (SocketException ignore) {
                        break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            serverHandler.start();

            Pattern validMessagePattern = Pattern.compile("\\S.*");

            while (true) {
                String input = reader.readLine();
                if ("!quit".equals(input)) {
                    outputStream.writeByte(0);
                    outputStream.writeUTF(username);
                    break;
                }
                if (!validMessagePattern.matcher(input).matches()) continue;

                outputStream.writeByte(1);
                outputStream.writeUTF("%s: %s".formatted(username, input));
                outputStream.flush();
            }
        } catch (IOException e) {
            System.err.println("Could not connect to the server");
        }
    }
}
