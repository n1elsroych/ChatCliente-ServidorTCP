package main;

import java.io.IOException;
import threads.Client;

public class ClientMain {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 8888;
    
    public static void main(String[] args) {
        try {
            Client client = new Client(SERVER_ADDRESS, PORT);
            client.connect();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
}
