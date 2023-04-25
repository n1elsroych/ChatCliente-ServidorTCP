package events;

import java.util.EventObject;

public class DisconnectionEvent extends EventObject{
    boolean disconnect;
    
    public DisconnectionEvent(Object source, boolean disconnect){
        super(source);
        this.disconnect = disconnect;
    }
    
    public boolean getDisconnect(){
        return disconnect;
    }
}
