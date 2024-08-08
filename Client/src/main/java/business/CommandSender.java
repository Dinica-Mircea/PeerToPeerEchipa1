package business;

import business.directMessages.DirectMessages;
import business.directMessages.TCPChatReceiver;
import utils.CommunicationConverter;
import utils.CommunicationProperties;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class CommandSender {
    private final DatagramSocket udpSocket;
    private final SocketHandler socketHandler;
    private String currentReceiver;
    private GroupHandler groupHandler;
    private DirectMessages directMessages;

    public CommandSender(SocketHandler socketHandler, GroupHandler groupHandler,DirectMessages directMessages) throws SocketException {
        this.socketHandler = socketHandler;
        this.udpSocket = new DatagramSocket();
        this.groupHandler = groupHandler;
        this.directMessages = directMessages;
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
            if (socketHandler.getSocketByNickname(nickname) == null) {
                clientSocket = new Socket(socketHandler.getIp(nickname), CommunicationProperties.PORT);
                socketHandler.addNewSocketIp(clientSocket, socketHandler.getIp(nickname));
                System.out.println(nickname + " connected");
                directMessages.startNewChat(clientSocket);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendCommand(String msg) {
        String[] split = msg.trim().split(" ");
        String command = split[0];
        switch (command) {
            case "!ack": {
                String nickname = split[1];
                sendAck(nickname, command);
                return;
            }
            case "!group": {
                String groupName = split[1];
                List<String> groupIps = new ArrayList<>();
                try {
                    String myIp = InetAddress.getLocalHost().getHostAddress().trim();
                    System.out.println("Creating group with ip: " + myIp);
                    groupIps.add(myIp);
                    groupHandler.addGroup(groupName, groupIps);
                } catch (UnknownHostException e) {
                    System.out.println("Can't get my ip.");
                }
                return;
            }
            case "!invite": {
                String groupName = split[1];
                String personToBeInvited = split[2];
                groupHandler.addNicknameInPendingGroup(groupName, personToBeInvited);
                sendGroupInvite(command, groupName, personToBeInvited);
                return;
            }
            case "!ackg": {
                String groupName = split[1];
                sendAcknowledgeGroup(groupName, command);
                return;
            }
            default: {
                String nickname = split[1];
                sendUdpMessage(command, nickname);
            }
        }
    }

    private void sendAcknowledgeGroup(String groupName, String command) {
        String inviterIp;
        if ((inviterIp = groupHandler.removeReceivedInvite(groupName)) != null) {
            Socket clientSocket;
            try {
                System.out.println(groupName + "trying to connect");
                DatagramPacket packet = CommunicationConverter.fromMessageGroupToPacket(
                        command, "", groupName, CommunicationProperties.SERVER_IP, CommunicationProperties.PORT);
                udpSocket.send(packet);
                if (socketHandler.getSocketByIp(inviterIp) == null) {
                    clientSocket = new Socket(inviterIp, CommunicationProperties.PORT);
                    socketHandler.addNewSocketIp(clientSocket, socketHandler.getIp(groupName));
                    System.out.println(groupName + " connected");
                    TCPChatReceiver tcpChatReceiver = new TCPChatReceiver(clientSocket, groupHandler);
                    tcpChatReceiver.start();
                } else {
                    System.out.println("Already connected with ip: " + inviterIp);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Didn't have the invitation for group " + groupName);
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
