import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
    private Socket connectionSocket;
    int port;
    private static int userCount = 0;
    boolean newUser;

    public Server(Socket connectionSocket){
        this.connectionSocket = connectionSocket;
        this.newUser = true;
    }

    public void run(){
        while(true) {
            if (newUser) {
                userCount++;
                System.out.println("User " + userCount + " has joined.");
                newUser = false;
            }
            try {
                processRequest();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void processRequest() throws IOException, ClassNotFoundException{
        InputStream is = connectionSocket.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Gamestate g = (Gamestate) ois.readObject();
        System.out.println(g.playerNumber + ": " + g.message);
     }
}
