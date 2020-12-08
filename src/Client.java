import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Scanner;

public class Client {

    Gamestate g;
    int ID;

    public static int generateID(){
        ZonedDateTime nowZoned = ZonedDateTime.now();
        Instant midnight = nowZoned.toLocalDate().atStartOfDay(nowZoned.getZone()).toInstant();
        Duration duration = Duration.between(midnight, Instant.now());
        long seconds = duration.getSeconds();
        return (int) (seconds%(65535-4999))+4999;
    }

    public void sendData() throws IOException {
        this.ID = generateID();
        System.out.println(ID);
        Socket sendingSocket = new Socket("localhost", 4999);
        OutputStream os;
        BufferedOutputStream bos;
        ObjectOutputStream oos;

        //Example Object
        this.g = new Gamestate(ID, "Hello World!");

        Scanner clientInputGetter = new Scanner(System.in);

        String message = this.g.message;

        this.g.message = message;
        os = sendingSocket.getOutputStream();
        bos = new BufferedOutputStream(os);
        oos = new ObjectOutputStream(bos);
        oos.writeObject(this.g);

        oos.flush();

        while(!message.equals("exit")){
            //Get gamestate back from server
            receiveData();
            message = clientInputGetter.next();
            this.g.message = message;
            os = sendingSocket.getOutputStream();
            bos = new BufferedOutputStream(os);
            oos = new ObjectOutputStream(bos);
            oos.writeObject(this.g);

            oos.flush();

        }
        sendingSocket.close();
    }

    public void receiveData() throws IOException {
        Socket receivingSocket;
        InputStream is;
        BufferedInputStream bis;
        ObjectInputStream ois;
        boolean listening = true;
        while(listening){
        ServerSocket clientListening = new ServerSocket(this.ID);
        receivingSocket = clientListening.accept();
        is = receivingSocket.getInputStream();
        bis = new BufferedInputStream(is);
        ois = new ObjectInputStream(bis);
            try {
                Gamestate temp = (Gamestate) ois.readObject();
                if(temp.playerNumber == ID) {
                    listening = false;
                    this.g = temp;
                }else{
                    System.out.println(temp.playerNumber + ": " + temp.message);
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Received incompatible Object Type from Server");
            }
            ois.close();
            bis.close();
            is.close();
            clientListening.close();
        }
            System.out.println(g.message);
    }

    public static void main(String[] args) throws IOException{
        Client client = new Client();
        client.sendData();
    }
}
