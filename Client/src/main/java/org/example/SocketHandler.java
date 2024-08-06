package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketHandler {
    private final ServerSocket serverSocket;
    private final Map<String, Socket> nicknameSocketsPair;
    private final Map<String,String> nicknameIpPair;

    public SocketHandler() throws IOException {
        serverSocket=new ServerSocket();
        nicknameSocketsPair=new ConcurrentHashMap<>();
        nicknameIpPair=new ConcurrentHashMap<>();
    }

    public Socket acceptNewClient() throws IOException {
        //synchronized (serverSocket){
            return serverSocket.accept();
        //}
    }

    public void addNewConnection(Socket clientSocket, String sender) {
        nicknameSocketsPair.put(sender, clientSocket);
    }

    public String getIp(String nickname) {
        return nicknameIpPair.get(nickname);
    }
}
