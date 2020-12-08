import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
    private Socket connectionSocket;
    private static int userCount = 0;
    boolean newUser;
    boolean userConnected;
    int userNumber;
    int port;

    public Server(Socket connectionSocket){
        this.connectionSocket = connectionSocket;
        this.newUser = true;
        userConnected = true;
    }

    public void run(){
        while(this.userConnected) {
            if (newUser) {
                userCount++;
                this.userNumber = userCount;
                System.out.println("User " + this.userNumber + " has joined.");
                newUser = false;
            }
            try {
                if(userConnected) {
                    if (processRequest() != 0) {
                        this.userConnected = false;
                        System.out.println("User " + this.userNumber + " has left.");
                        userCount--;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private int processRequest() throws IOException, ClassNotFoundException{
        InputStream is = connectionSocket.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(bis);
        }catch(EOFException e){
            return 1;
        }
        Gamestate g = (Gamestate) ois.readObject();
        if(g.message.equals("exit"))
            return 1;
        System.out.println(g.playerNumber + ": " + g.message);
        return 0;
     }
}
