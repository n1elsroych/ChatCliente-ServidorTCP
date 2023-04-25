package events;

import java.util.EventObject;

public class ClientDisconnectedEvent extends EventObject{
    int id;
    
    public ClientDisconnectedEvent(Object source, int id){
        super(source);
        this.id = id;
    }
    
    public int getId(){
        return id;
    }
}
