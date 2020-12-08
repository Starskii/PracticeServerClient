import java.io.Serializable;

public class Gamestate implements Serializable {
    public String message;
    public int playerNumber;
    public Gamestate(int playerNumber, String message){
        this.playerNumber = playerNumber;
        this.message = message;
    }
}
