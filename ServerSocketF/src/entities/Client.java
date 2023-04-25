package entities;

import java.net.Socket;

public class Client {
    Socket socket;
    String nickname;
    int id;
    
    public Client(Socket socket){
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
