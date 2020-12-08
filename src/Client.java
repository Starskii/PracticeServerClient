import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException{
        Socket s = new Socket("localhost", 4999);
        OutputStream os = s.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(os);
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        //Example Object
        Gamestate g = new Gamestate(1, "Hello World!");

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
    }
}
