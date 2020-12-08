import java.io.*;
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
        Socket s = new Socket("localhost", 4999);
        OutputStream os;
        BufferedOutputStream bos;
        ObjectOutputStream oos;

        //Example Object
        Gamestate g = new Gamestate(ID, "Hello World!");

        Scanner clientInputGetter = new Scanner(System.in);

        String message = g.message;
        while(!message.equals("exit")){
            g.message = message;
            os = s.getOutputStream();
            bos = new BufferedOutputStream(os);
            oos = new ObjectOutputStream(bos);
            oos.writeObject(g);

            oos.flush();

            message = clientInputGetter.next();
        }
        s.close();
    }
}
