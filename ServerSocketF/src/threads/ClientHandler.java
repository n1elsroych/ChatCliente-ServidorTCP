package threads;

import events.ClientDisconnectedEvent;
import events.ClientNicknamedEvent;
import events.MessageReceivedEvent;
import events.ServerEventsListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ClientHandler extends Thread{
    private DataInputStream in;
    private ArrayList<ServerEventsListener> listeners;
    
    private int clientID;
    
    public ClientHandler(InputStream inputStream, int id){
        in = new DataInputStream(inputStream);
        listeners = new ArrayList<>();
        
        clientID = id;
    }
    
    @Override
    public void run() {
        receiveNickname();
        
        boolean isConnected = true;
        while (isConnected) {
            try {
                String message = in.readUTF(); //Si aqui falla le llegara el id en el message?
                if (isDisconnectionRequest(message)){
                    int id = Integer.parseInt(getData("<origin>", message));
                    System.out.println("El cliente con ID = "+id+" solicito desconectarse");
                    isConnected = false;
                }
                triggerMessageReceivedEvent(message);
            } catch (IOException ex) {
                ex.printStackTrace();
                isConnected = false;
                
                triggerClientDisconnectedEvent(clientID);
            }
        }
    }
    
    private String getData(String type, String dataMessage){
        int i = dataMessage.indexOf(type) + type.length();
        int f = dataMessage.indexOf(";", i);
        
        return dataMessage.substring(i, f);
    }
    
    private boolean isDisconnectionRequest(String message){
        return getData("<message>", message).equals("/salir");
    }
    
    private void receiveNickname(){
        try {
            String sessionData = in.readUTF(); //Y que pasa si el cliente corta la conexion (cierra el programa)
            //y ya no manda siquiera el nickname junto su id? pero este hilo sigue esperando un mensaje.
            //Por eso al crear un ClientHandler debo pasarle tambien el id, ademas del inpuStream del socket
            //por que sino como le digo al Server que lo quite de su lista (revisar onUserConnected)
            //Ya que puede suceder que un cliente aun no mande su nickname y mientras otros clientes se han conectado
            //por lo tanto se pierde la referencia a dicho cliente
            clientID = Integer.parseInt(getData("<id>", sessionData));
            triggerClientNicknamedEvent(sessionData);
        } catch (IOException ex) {
            ex.printStackTrace();
            triggerClientDisconnectedEvent(clientID);
        }
    }
    
    public void addEventsListener(ServerEventsListener listener){
        listeners.add(listener);
    }
    
    public void removeMiEventoListener(ServerEventsListener listener) {
        listeners.remove(listener);
    }
    
    public void triggerMessageReceivedEvent(String message) {
        MessageReceivedEvent evt = new MessageReceivedEvent(this, message);
        for (ServerEventsListener listener : listeners) {
            listener.onReceivedMessage(evt);
        }
    }
    
    public void triggerClientNicknamedEvent(String sessionData){
        ClientNicknamedEvent evt = new ClientNicknamedEvent(this, sessionData);
        for (ServerEventsListener listener : listeners){
            listener.onClientNicknamed(evt);
        }
    }
    
    public void triggerClientDisconnectedEvent(int id) {
        ClientDisconnectedEvent evt = new ClientDisconnectedEvent(this, id);
        for (ServerEventsListener listener : listeners) {
            listener.onClientDisconnected(evt);
        }
    }
}
