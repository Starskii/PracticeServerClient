import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server extends Thread{
    private Socket connectionSocket;
    private static int userCount = 0;
    private static LinkedList<Integer> userList;
    boolean newUser;
    boolean userConnected;
    int userNumber;
    int port;

    public Server(Socket connectionSocket){
        this.connectionSocket = connectionSocket;
        userList = new LinkedList<>();
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
        ois = new ObjectInputStream(bis);
        Gamestate g = (Gamestate) ois.readObject();
        if(!userList.contains(g.playerNumber))
            userList.add(g.playerNumber);
        if(g.message.equals("exit")) {
            userList.remove(g.playerNumber);
            return 1;
        }
        System.out.println(g.playerNumber + ": " + g.message);
        return 0;
     }
}
