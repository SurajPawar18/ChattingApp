import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private BufferedReader br;
    private PrintWriter out;
    private Socket socket;

    public Client() {
        try {
            System.out.println("Sending request to server...");
            socket = new Socket("127.0.0.1", 7777);
            System.out.println("Connection established...");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            startReading();
            startWriting();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startReading() {
        // Thread to continuously read messages from the server
        Runnable reader = () -> {
            System.out.println("Reader Started...");
            try {
                String msg;
                while ((msg = br.readLine()) != null) {
                    if (msg.equals("exit")) {
                        System.out.println("Server terminated the chat");
                        break;
                    }
                    System.out.println("Server: " + msg);
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

    public void startWriting() {
        // Thread to continuously read user input and send it to the server
        System.out.println("Writer Started..");
        Runnable writer = () -> {
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

    public static void main(String[] args) {
        System.out.println("This is client");
        new Client();
    }
}
