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
        synchronized (serverSocket) {
            Socket socket = serverSocket.accept();
            addNewSocketIpNickname(socket, ip, nickname);
            return socket;
        }
    }

    public void addNewSocketIp(Socket clientSocket, String ip) {
        nicknameSocketsPair.put(ip, clientSocket);
    }
    public void addNewSocketIpNickname(Socket clientSocket, String ip, String nickname) {
        addNewIpNickname(ip, nickname);
        addNewSocketIp(clientSocket, ip);
    }

    public String getIp(String nickname) {
        return nicknameIpPair.get(nickname);
    }

    public void addNewIpNickname(String ip, String nickname) {
        synchronized (nicknameIpPair) {
            nicknameIpPair.put(nickname, ip);
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
