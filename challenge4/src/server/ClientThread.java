package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ClientThread extends Thread {
    private final Socket socket;
    private boolean started, finished;
    private final ArrayList<String> data;
    private DataOutputStream outStream;

    public ClientThread(Socket socket) {
        this.socket = socket;
        finished = false;
        started = false;
        data = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            DataInputStream inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());
            String clientMessage;
            while (!finished) {
                switch (inStream.readByte()) {
                    case 0:
                        data.add("User \"%s\" has left the server".formatted(inStream.readUTF()));
                        finished = true;
                        break;
                    case 2:
                        started = true;
                    case 1:
                        clientMessage = inStream.readUTF();
                        data.add(clientMessage);
                        break;
                    default:
                        throw new RuntimeException("Unexpected message code");
                }
            }
            inStream.close();
            outStream.close();
            socket.close();
        } catch (SocketException ignore) {
            finished = true;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> retrieveData() {
        ArrayList<String> data = new ArrayList<>(this.data);
        this.data.clear();
        return data;
    }

    public void sendMessage(String message) {
        try {
            outStream.writeUTF(message);
            outStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isFinished() {
        return finished && data.isEmpty();
    }

    public boolean hasStarted() {
        return started;
    }
}
