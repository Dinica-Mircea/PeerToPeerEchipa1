package business;

import utils.CommunicationProperties;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//@Component
public class SocketHandler {

    private final ServerSocket serverSocket;
    private final Map<String, Socket> ipSocketsPair;
    private final Map<String, String> nicknameIpPair;

    public SocketHandler() throws IOException {
        System.out.println("Socket handler started");
        serverSocket = new ServerSocket(CommunicationProperties.PORT);
        ipSocketsPair = new ConcurrentHashMap<>();
        nicknameIpPair = new ConcurrentHashMap<>();
    }

    public Socket acceptNewClient(String nickname, String ip) throws IOException {
        synchronized (serverSocket) {
            Socket socket = serverSocket.accept();
            addNewSocketIpNickname(socket, ip, nickname);
            return socket;
        }
    }

    public Socket acceptNewClient(String ip) throws IOException {
        synchronized (serverSocket) {
            Socket socket = serverSocket.accept();
            addNewSocketIp(socket, ip);
            return socket;
        }
    }

    public void addNewSocketIp(Socket clientSocket, String ip) {
        System.out.println("New socket added for " + ip);
        ipSocketsPair.put(ip, clientSocket);
    }

    public void addNewSocketIpNickname(Socket clientSocket, String ip, String nickname) {
        addNewIpNickname(ip, nickname);
        addNewSocketIp(clientSocket, ip);
    }

    public String getIpFromNickname(String nickname) {
        return nicknameIpPair.get(nickname);
    }

    public void addNewIpNickname(String ip, String nickname) {
        synchronized (nicknameIpPair) {
            nicknameIpPair.put(nickname, ip);
        }
    }

    public Socket getSocketByNickname(String nickname) {
        String ip = nicknameIpPair.get(nickname);
        if (ip == null)
            return null;
        synchronized (ipSocketsPair) {
            if (ipSocketsPair.containsKey(ip)) {
                return ipSocketsPair.get(ip);
            }
        }
        return null;
    }

    public Socket getSocketByIp(String ip) {
        synchronized (ipSocketsPair) {
            if (ipSocketsPair.containsKey(ip)) {
                return ipSocketsPair.get(ip);
            }
        }
        return null;
    }

    public void remove(String nickname) {
        String ip;
        if ((ip = nicknameIpPair.get(nickname)) != null) {
            try {
                ipSocketsPair.remove(ip).close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            nicknameIpPair.remove(nickname);
        } else {
            System.out.println("No ip for " + nickname);
        }
    }
}
