import java.io.IOException;
import java.net.ServerSocket;

public class MultiServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;
        Server w;

        try {
            serverSocket = new ServerSocket(4999);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4999.");
            System.exit(-1);
        }

        while (listening) {
            w = new Server(serverSocket.accept());
            Thread t = new Thread(w);
            t.start();
        }
    }
}
