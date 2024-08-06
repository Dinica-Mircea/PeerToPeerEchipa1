package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketHandler {
    private final ServerSocket serverSocket;
    private Map<String, Socket> nicknameSocketsPair;

    public SocketHandler() throws IOException {
        serverSocket=new ServerSocket();
        nicknameSocketsPair=new ConcurrentHashMap<>();
    }

    public Socket acceptNewSocket() throws IOException {
        synchronized (serverSocket){
            return serverSocket.accept();
        }
    }

    public void addNewConnection(Socket clientSocket, String sender) {
        nicknameSocketsPair.put(sender, clientSocket);
    }
}
