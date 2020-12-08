import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends Thread{
    private Socket connectionSocket;
    private static int userCount = 0;
    boolean newUser;
    boolean userConnected;
    int userNumber;
    Gamestate g;
    int currentPlayer;
    ConcurrentHashMap<Integer, Integer> userList;

    public Server(Socket connectionSocket, ConcurrentHashMap<Integer, Integer> userList){
        this.userList = userList;
        this.connectionSocket = connectionSocket;
        g = new Gamestate(0, "");
        this.newUser = true;
        userConnected = true;
        currentPlayer = 0;
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

    private void updatePlayersGamestate(Gamestate g) throws IOException {
        for(Integer playerID : userList.values()){
            try {
                Thread.sleep(10);
                Socket dataSocket = new Socket(connectionSocket.getInetAddress(), playerID);
                OutputStream os = dataSocket.getOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream(os);
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(g);
                oos.close();
                bos.close();
                os.close();
                dataSocket.close();
            }catch(Exception e){
                System.out.println("Player " + playerID + " not listening to server.");
            }
        }
    }

    private void updateGameState(Gamestate g){
        if(userList.size() < 4){
            this.g = new Gamestate(0, g.message);
        }else{
            currentPlayer = userList.get(0);
            this.g = new Gamestate(currentPlayer, g.message);
        }
    }

    private int processRequest() throws IOException, ClassNotFoundException{
        InputStream is = connectionSocket.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        ObjectInputStream ois;
        ois = new ObjectInputStream(bis);
        Gamestate g = (Gamestate) ois.readObject();
        updateGameState(g);
        if(!userList.contains(g.playerNumber))
            userList.put(g.playerNumber, g.playerNumber);

        updatePlayersGamestate(g);
        if(g.message.equals("exit")) {
            userList.remove(g.playerNumber);
            return 1;
        }
        System.out.println(g.playerNumber + ": " + g.message);
        return 0;
     }
}
