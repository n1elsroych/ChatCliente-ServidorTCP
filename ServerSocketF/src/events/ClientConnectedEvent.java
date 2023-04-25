package events;

import java.net.Socket;
import java.util.EventObject;

public class ClientConnectedEvent extends EventObject{
    private Socket socket;
    private String nickname;
    private int id;
    //aqui vendran atributos como nickname, password, etc
    
    public ClientConnectedEvent(Object source, Socket socket){
        super(source);
        this.socket = socket;
        //this.id = id;
    }
    
    public Socket getSocket(){
        return socket;
    }

    public String getNickname() {
        return nickname;
    }

    public int getId() {
        return id;
    }    
}
