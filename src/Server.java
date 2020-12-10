import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;


public class Server extends Thread{
    private Socket connectionSocket;
    private static int userCount = 0;
    private int currentTurn = -1;
    boolean newUser;
    boolean userConnected;
    int userNumber;
    Object g;
    int currentPlayer;
    ConcurrentHashMap<Integer, Integer> userList;

    public Server(Socket connectionSocket, ConcurrentHashMap<Integer, Integer> userList){
        this.userList = userList;
        this.connectionSocket = connectionSocket;
        g = new Object();
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

    private void updatePlayersGamestate(Object g) throws IOException {
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
                System.out.println("Core.Player " + playerID + " not listening to server.");
            }
        }
    }

    private void updateGameState(Object g){
        if(userList.size() <= 3){
            this.g = new Object();
        }else{
            if(currentTurn >= 4)
                currentTurn = 0;
            currentPlayer = userList.get(currentTurn);
            this.g = new Object();
        }
    }

    private int processRequest() throws IOException, ClassNotFoundException{
        InputStream is = connectionSocket.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        ObjectInputStream ois;
        ois = new ObjectInputStream(bis);
        String message = (String) ois.readObject();
        String gameState[] = message.split(":");
        String ID = gameState[0];
        System.out.println("Player: " + ID + " sent gamestate: ");
        for(int i = 1; i < gameState.length; i++){
            String countryInfo[] = gameState[i].split(" ");
            System.out.println("Country " + countryInfo[0] + " owned by player " + countryInfo[1] + " has " + countryInfo[2] + " armies.");
        }
        System.out.println(message);
//        if(!userList.values().contains(activePlayerID))
//            userList.put(userList.size(), activePlayerID);
        if(userList.size() == 4)
            incrementTurn();
        updateGameState(g);
        updatePlayersGamestate(this.g);
//        System.out.println(activePlayerID + ": has updated the gamestate");
        return 0;
    }

    private synchronized void incrementTurn(){
        currentTurn++;
    }
}
