import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket server;
    private Socket socket;
    private BufferedReader br;
    private PrintWriter out;

    public Server() {
        try {
            server = new ServerSocket(7777);
            System.out.println("Server is ready to accept connections..");
            System.out.println("Waiting..");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            startReading();
            startWriting();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startWriting() {
        // Thread to continuously read user input and send it to the client
        Runnable writer = () -> {
            System.out.println("Writer Started...");
            try {
                BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                String content;
                while ((content = br1.readLine()) != null && !socket.isClosed()) {
                    out.println(content);
                    out.flush();
                    if (content.equals("exit")) {
                        socket.close();
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        new Thread(writer).start();
    }

    public void startReading() {
        // Thread to continuously read messages from the client
        Runnable reader = () -> {
            System.out.println("Reader Started..");
            try {
                String msg;
                while ((msg = br.readLine()) != null) {
                    if (msg.equals("exit")) {
                        System.out.println("Client terminated the chat");
                        break;
                    }
                    System.out.println("Client: " + msg);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(reader).start();
    }

    public static void main(String[] args) {
        System.out.println("This is server.. going to start server");
        new Server();
    }
}
