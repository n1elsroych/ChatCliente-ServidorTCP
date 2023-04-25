package events;

import java.util.EventObject;

public class MessageReceivedEvent extends EventObject{
    private String message;
    
    public MessageReceivedEvent(Object source, String message){
        super(source);
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}
