package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) {
        int port = 8263;
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.printf("Started server using port %s%n", port);

            List<ClientThread> clients = Collections.synchronizedList(new ArrayList<>());

            AtomicBoolean keepAlive = new AtomicBoolean(true);

            Thread clientHandler = new Thread(() -> {
                ClientThread client;
                while (true) {
                    try {
                        client = new ClientThread(server.accept());
                    } catch (SocketException ignore) {
                        break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    synchronized (clients) {
                        clients.add(client);
                    }
                    client.start();
                    keepAlive.set(false);
                }
            });

            clientHandler.start();

            while (keepAlive.get() || !clients.isEmpty()) {
                synchronized (clients) {
                    clients.removeIf(ClientThread::isFinished);
                    for (ClientThread client : clients) {
                        ArrayList<String> data = client.retrieveData();
                        data.forEach(System.out::println);
                        for (ClientThread c : clients) {
                            if (c != client && c.hasStarted()) data.forEach(c::sendMessage);
                        }
                    }
                }
            }

            System.out.println("All users have left the server");
        } catch (IOException e) {
            System.err.printf("Failed to start server using port %s%n", port);
            throw new RuntimeException(e);
        }
    }
}
