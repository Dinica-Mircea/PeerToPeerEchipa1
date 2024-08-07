package business;

import utils.CommunicationProperties;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketHandler {
    private final ServerSocket serverSocket;
    private final Map<String, Socket> nicknameSocketsPair;
    private final Map<String, String> nicknameIpPair;

    public SocketHandler() throws IOException {
        serverSocket = new ServerSocket(CommunicationProperties.PORT);
        nicknameSocketsPair = new ConcurrentHashMap<>();
        nicknameIpPair = new ConcurrentHashMap<>();
    }

    public Socket acceptNewClient(String nickname, String ip) throws IOException {
        //synchronized (serverSocket){
        Socket socket = serverSocket.accept();
        addNewConnection(socket, nickname);
        addNewIp(nickname, ip);
        return socket;
        //}
    }

    public void addNewConnection(Socket clientSocket, String sender) {
        nicknameSocketsPair.put(sender, clientSocket);
    }

    public String getIp(String nickname) {
        return nicknameIpPair.get(nickname);
    }

    public void addNewIp(String sender, String ip) {
        synchronized (nicknameIpPair) {
            nicknameIpPair.put(sender, ip);
        }
    }

    public Socket getSocket(String nickname) {
        synchronized (nicknameSocketsPair) {
            return nicknameSocketsPair.get(nickname);
        }
    }

    public void remove(String nickname) {
        synchronized (nicknameSocketsPair) {
            nicknameSocketsPair.remove(nickname);
        }
    }
}
