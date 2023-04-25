package events;

import java.util.EventObject;

public class ClientNicknamedEvent extends EventObject{
    String sessionData;
    
    public ClientNicknamedEvent(Object source, String sessionData){
        super(source);
        this.sessionData = sessionData;
    }

    public String getSessionData() {
        return sessionData;
    }
}
