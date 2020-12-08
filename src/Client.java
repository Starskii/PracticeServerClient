import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException{
        Socket s = new Socket("localhost", 4999);
        OutputStream os = s.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);

        //Example Object
        Gamestate g = new Gamestate(1, "Hello World!");

        Scanner clientInputGetter = new Scanner(System.in);

        String message = g.message;
        while(!message.equals("exit")){
            g.message = message;
            oos.writeObject(g);
            message = clientInputGetter.next();
        }
    }
}
