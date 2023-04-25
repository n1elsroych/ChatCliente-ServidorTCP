package threads;

import events.ClientEventsListener;
import events.DisconnectionEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class OutputHandler extends Thread{
    DataOutputStream out;
    private int id;
    
    private ArrayList<ClientEventsListener> listeners;
    
    public OutputHandler(OutputStream outputStream, int id){
        out = new DataOutputStream(outputStream);
        listeners = new ArrayList<>();
        this.id = id;
    }
    
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean connected = true;
        while (connected) {
            String message = scanner.nextLine();
            try {
                out.writeUTF("<origin>"+id+";<message>"+message+";");
                if (message.equals("/salir")){
                    connected = false;
                    triggerDisconnectedEvent(true);
                }
            } catch(IOException ex) {
                ex.printStackTrace();
                triggerDisconnectedEvent(true);
                connected = false;
            }
        }
    }
    
    public void addEventsListener(ClientEventsListener listener){
        listeners.add(listener);
    }
    
    public void removeMiEventoListener(ClientEventsListener listener) {
        listeners.remove(listener);
    }
    
    public void triggerDisconnectedEvent(boolean disconnect) {
        DisconnectionEvent evt = new DisconnectionEvent(this, disconnect);
        for (ClientEventsListener listener : listeners) {
            listener.onDisconnected(evt);
        }
    }
    
    //quiza aqui pueda estar el defineNickname()
}
