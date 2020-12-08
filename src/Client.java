import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Scanner;

public class Client {

    public static int generateID(){
        ZonedDateTime nowZoned = ZonedDateTime.now();
        Instant midnight = nowZoned.toLocalDate().atStartOfDay(nowZoned.getZone()).toInstant();
        Duration duration = Duration.between(midnight, Instant.now());
        long seconds = duration.getSeconds();
        return (int) (seconds%(65535-4999))+4999;
    }

    public static void main(String[] args) throws IOException{
        int ID = generateID();
        System.out.println(ID);
        Socket sendingSocket = new Socket("localhost", 4999);
        Socket receivingSocket;
        OutputStream os;
        InputStream is;
        BufferedOutputStream bos;
        BufferedInputStream bis;
        ObjectOutputStream oos;
        ObjectInputStream ois;

        //Example Object
        Gamestate g = new Gamestate(ID, "Hello World!");

        Scanner clientInputGetter = new Scanner(System.in);

        String message = g.message;
        while(!message.equals("exit")){
            g.message = message;
            os = sendingSocket.getOutputStream();
            bos = new BufferedOutputStream(os);
            oos = new ObjectOutputStream(bos);
            oos.writeObject(g);

            oos.flush();

            //Get gamestate back from server
            boolean listening = true;
            while(listening){
                ServerSocket clientListening = new ServerSocket(ID);
                receivingSocket = clientListening.accept();
                is = receivingSocket.getInputStream();
                bis = new BufferedInputStream(is);
                ois = new ObjectInputStream(bis);
                try {
                    g = (Gamestate) ois.readObject();
                    listening = false;
                } catch (ClassNotFoundException e) {
                    System.out.println("Received incompatible Object Type from Server");
                }
                ois.close();
                bis.close();
                is.close();
                System.out.println(g.message);
            }
            message = clientInputGetter.next();
        }
        sendingSocket.close();
    }
}
