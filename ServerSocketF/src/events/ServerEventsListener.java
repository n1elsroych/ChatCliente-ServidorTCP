package events;

import java.util.EventListener;

public interface ServerEventsListener extends EventListener{
    
    void onUserConnected(ClientConnectedEvent evt);
    
    void onReceivedMessage(MessageReceivedEvent evt);

    void onClientDisconnected(ClientDisconnectedEvent evt);
    
    void onClientNicknamed(ClientNicknamedEvent evt);
}
