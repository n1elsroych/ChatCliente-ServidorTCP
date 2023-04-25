package entities;

import threads.*;
import events.ClientConnectedEvent;
import events.ClientDisconnectedEvent;
import events.ClientNicknamedEvent;
import events.MessageReceivedEvent;
import events.ServerEventsListener;
import java.io.DataOutputStream;
import java.io.IOException;
//import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server implements ServerEventsListener{
    private ServerSocket serverSocket;
    private Map<Integer, Client> clients;
    ConnectionsHandler connectionsHandler;
    DisconnectionsHandler disconnectionsHandler;
    
    private int clientId;
    
    public Server(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        clients = new HashMap<>();
        clientId = 0;
        
        System.out.println("Servidor iniciado en el puerto " + port);
    }
    
    public void start() throws IOException{
        connectionsHandler = new ConnectionsHandler(serverSocket);
        connectionsHandler.addEventsListener(this);
        connectionsHandler.start();
        
        disconnectionsHandler = new DisconnectionsHandler(clients);
        disconnectionsHandler.addEventsListener(this);
        disconnectionsHandler.start();
    }
    
    private String getData(String type, String dataMessage){
        int i = dataMessage.indexOf(type) + type.length();
        int f = dataMessage.indexOf(";", i);
        
        return dataMessage.substring(i, f);
    }
    
//    private synchronized void sendBroadcast(String data) throws IOException{
//        DataOutputStream out;
//        int originID = Integer.parseInt(getData("<origin>", data));
//        String originNickname = clients.get(originID).getNickname();
//        
//        String message = getData("<message>", data);
//        for (Client client: clients.values()) {
//            Socket clientSocket = client.getSocket();
//            out = new DataOutputStream(clientSocket.getOutputStream());
//            out.writeUTF(originNickname+": "+message);
//        }
//    }
    
    private void sendBroadcast(String data) throws IOException{
        DataOutputStream out;
        int originID = Integer.parseInt(getData("<origin>", data));
        synchronized (clients) {
            String originNickname = clients.get(originID).getNickname();
            String message = getData("<message>", data);
            for (Client client: clients.values()) {
                Socket clientSocket = client.getSocket();
                out = new DataOutputStream(clientSocket.getOutputStream());
                out.writeUTF(originNickname+": "+message);
            }
        }
            
    }
    
//    private synchronized void addClient(Client client){
//        clients.put(client.getId(), client);
//    }
 
    @Override
    public void onUserConnected(ClientConnectedEvent evt) {
        try {
            Socket socket = evt.getSocket();
            Client client = new Client(socket);
            clientId++;
            client.setId(clientId);
            
            //Talvez no debo guardar el client en la lista antes de que reciba su nickname
            //Pero como ejecuto client.getSocket().close() ? si pierdo su referencia
            //ya que constantemente se conectan nuevos clientes
            //Una opcion: no permitir que mas clientes se conecten hasta que el actual no mande su nickname
            //Seg opcion: mandar al constructor ClientHandler el id del client para quitarlo de la lista, mientras
            //              otros clientes se conectan, y con este id quitarlo de la lista 
            //              (ojo: analizar que pasa en sendBroadcast si el cliente aun no mandado su nickname
            //              pero aun asi ya estaria en la lista, Pero su inputStream es un buffer no? se guardarian
            //              todos los mensajes recibidos y con flush() quiza se muestren los mensajes que no vio porque
            //              seguia "iniciando sesion" (mandando su nickname))
            synchronized (clients) {
                clients.put(clientId, client);
            }
            //addClient(client);
            
            ClientHandler clientHandler = new ClientHandler(socket.getInputStream(), clientId);
            clientHandler.addEventsListener(this);
            clientHandler.start();
            
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("<id>"+clientId+";");
                        
            System.out.println("El usuario ha sido registrado con el ID = "+clientId);
        } catch(IOException ex) {
            ex.printStackTrace();
            //clientId--;
        }
    }
    
    @Override
    public void onReceivedMessage(MessageReceivedEvent evt) {
        String message = evt.getMessage();
        if (isDisconnectionRequest(message)){
            int id = Integer.parseInt(getData("<origin>", message));
            ClientDisconnectedEvent disconnectionEvt = new ClientDisconnectedEvent(this, id);
            onClientDisconnected(disconnectionEvt);
        } else {
            try {
                sendBroadcast(message); 
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private boolean isDisconnectionRequest(String message){
        return getData("<message>", message).equals("/salir");
    }

//    private synchronized void delClient(int id){
//        clients.remove(id);
//    }
    
    @Override
    public void onClientDisconnected(ClientDisconnectedEvent evt) {
        int id = evt.getId();
        synchronized(clients){// Analizar si esto funciona ya que DisconnectionsHandler bloquea clients y provoca la eliminacion de la lista de un client desconectado
            //pero ClientHandler tambien provoca que se elimine un client de la lista, por lo que si ocurre una desconexion
            //por medio de client handler y otra por disconnectionsHandler, solo uno debe poder acceder no??
            try {
                clients.get(id).getSocket().close();
                clients.remove(id);
                System.out.println("El cliente  con ID = "+id+" ya no esta conectado");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //delClient(id);
    }

    @Override
    public void onClientNicknamed(ClientNicknamedEvent evt) {
        String sessionData = evt.getSessionData();
        int id = Integer.parseInt(getData("<id>", sessionData));
        String nickname = getData("<nickname>", sessionData);
        synchronized (clients) {
            Client client = clients.get(id);
            client.setNickname(nickname);
        }
    }
}
