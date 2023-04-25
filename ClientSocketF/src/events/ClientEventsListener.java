package events;

public interface ClientEventsListener {
    
    void onReceivedMessage(MessageReceivedEvent evt);
    
    void onDisconnected(DisconnectionEvent evt);
}
