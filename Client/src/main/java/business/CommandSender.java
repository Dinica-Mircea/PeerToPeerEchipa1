package business;

import business.directMessages.TCPChatReceiver;
import utils.CommunicationConverter;
import utils.CommunicationProperties;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandSender {
    private final DatagramSocket udpSocket;
    private final SocketHandler socketHandler;
    private String currentReceiver;
    private GroupHandler groupHandler;

    public CommandSender(SocketHandler socketHandler,GroupHandler groupHandler) throws SocketException {
        this.socketHandler = socketHandler;
        this.udpSocket = new DatagramSocket();
        this.groupHandler = new GroupHandler();
    }

    public void sendEcho(String msg) throws IOException {
        if (msg.startsWith("!")) {
            sendCommand(msg);
        } else if (msg.startsWith("#")) {
            updateCurrentReceiver(msg);
        } else {
            sendDirectMessage(msg);
        }
    }

    private void sendDirectMessage(String msg) throws IOException {
        Socket socket = socketHandler.getSocketByNickname(currentReceiver);
        if (socket == null) {
            System.out.println("No existing ip for " + currentReceiver);
        } else {
            OutputStream out = socket.getOutputStream();
            String json = CommunicationConverter.fromMessageToJson(currentReceiver, msg);
            out.write(json.getBytes());
        }
    }

    private void updateCurrentReceiver(String msg) {
        String nextReceiver = msg.replace("#", "");
        if (socketHandler.getIp(nextReceiver) != null) {
            currentReceiver = nextReceiver;
            System.out.println("current receiver updated: " + currentReceiver);
        } else {
            System.out.println(nextReceiver + " not connected");
        }
    }

    private void sendAck(String nickname, String command) {
        Socket clientSocket;
        try {
            System.out.println(nickname + "trying to connect");
            DatagramPacket packet = CommunicationConverter.fromMessageToPacket(command, nickname, CommunicationProperties.SERVER_IP, CommunicationProperties.PORT);
            udpSocket.send(packet);
            clientSocket = new Socket(socketHandler.getIp(nickname), CommunicationProperties.PORT);
            socketHandler.addNewSocketIp(clientSocket, socketHandler.getIp(nickname));
            System.out.println(nickname + " connected");
            TCPChatReceiver tcpChatReceiver = new TCPChatReceiver(clientSocket, groupHandler);
            tcpChatReceiver.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendCommand(String msg) {
        String[] split = msg.trim().split(" ");
        String nickname = split[1];
        String command = split[0];
        switch (command) {
            case "!ack": {
                sendAck(nickname, command);
                return;
            }
            case "!group": {
                List<String> groupIps = new ArrayList<>();
                try {
                    String myIp = InetAddress.getLocalHost().getHostAddress().trim();
                    groupIps.add(myIp);
                    groupHandler.addGroup(nickname, groupIps);
                } catch (UnknownHostException e) {
                    System.out.println("Can't get my ip.");
                }
                return;
            }
            case "!invite": {
                groupHandler.addNicknameInPendingGroup(nickname, socketHandler.getIp(split[2]));
                sendGroupInvite(command, nickname, split[2]);
                return;
            }
            default:
                sendUdpMessage(command, nickname);
        }
    }

    private void sendGroupInvite(String command, String groupNickname, String receiver) {
        try {
            DatagramPacket packet = CommunicationConverter.fromMessageGroupToPacket(command, receiver, groupNickname, CommunicationProperties.SERVER_IP, CommunicationProperties.PORT);
            udpSocket.send(packet);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendUdpMessage(String command, String nickname) {
        try {
            DatagramPacket packet = CommunicationConverter.fromMessageToPacket(command, nickname, CommunicationProperties.SERVER_IP, CommunicationProperties.PORT);
            udpSocket.send(packet);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void close() {
        udpSocket.close();
    }
}
