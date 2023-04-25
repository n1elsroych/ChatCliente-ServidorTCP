package threads;

import events.ClientConnectedEvent;
import events.ServerEventsListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionsHandler extends Thread{
    ServerSocket serverSocket;
    
    private ArrayList<ServerEventsListener> listeners;
    
    public ConnectionsHandler(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
        listeners = new ArrayList<>();
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Esperando que un usuario se conecte...");
                Socket client = serverSocket.accept();
                System.out.println("Un usuario se ha conectado");
                triggerClientConnectedEvent(client);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void addEventsListener(ServerEventsListener listener){
        listeners.add(listener);
    }
    
    public void removeMiEventoListener(ServerEventsListener listener) {
        listeners.remove(listener);
    }
    
    public void triggerClientConnectedEvent(Socket socket) {
        ClientConnectedEvent evt = new ClientConnectedEvent(this, socket);
        for (ServerEventsListener listener : listeners) {
            listener.onUserConnected(evt);
        }
    }
}
