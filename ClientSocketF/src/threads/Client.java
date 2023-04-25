package threads;

import events.ClientEventsListener;
import events.DisconnectionEvent;
import events.MessageReceivedEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client implements ClientEventsListener{
    private Socket socket;
    private int id;
    
    public Client(String serverAddress, int port) throws IOException{
        socket = new Socket(serverAddress, port);
    }
    
    public void connect() throws IOException{
        defineNickname();
        
        InputHandler inputHandler = new InputHandler(socket.getInputStream());
        inputHandler.addEventsListener(this);
        inputHandler.start();
        
        OutputHandler outputHandler = new OutputHandler(socket.getOutputStream(), id);
        outputHandler.addEventsListener(this);
        outputHandler.start();
    }
    
    private void defineNickname() throws IOException{
        DataInputStream in = new DataInputStream(socket.getInputStream());
        String data = in.readUTF();
        if (data.contains("<id>")){
            int i = data.indexOf("<id>") + "<id>".length();
            int f = data.indexOf(";");
            int id = Integer.parseInt(data.substring(i, f));
            this.id = id;
            System.out.println("Se me ha asignado el id = "+id);
            
            System.out.println("Introduzca su nombre de usuario: ");
            Scanner scanner = new Scanner(System.in);
            String nickname = scanner.nextLine();
            data = data + "<nickname>"+nickname+";";
            
            System.out.println(data);
            
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(data);
        }
    }

    @Override
    public void onReceivedMessage(MessageReceivedEvent evt) {
        String message = evt.getMessage();
        System.out.println("Servidor: "+message);
    }

    @Override
    public void onDisconnected(DisconnectionEvent evt) {
        try {
            socket.close();
            System.out.println("Te has desconectado");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
