package business;

import business.directMessages.DirectMessages;
import domain.Message;
import utils.CommunicationConverter;
import utils.CommunicationProperties;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class UDPCommandReceiver {
    private final DatagramSocket socket;
    private boolean stillRunning;
    private byte[] buf = new byte[1024];
    List<String> pendingUsers;
    Map<String, String> pendingGroups;
    SocketHandler socketHandler;
    DirectMessages directMessages;
    GroupHandler groupHandler;

    public UDPCommandReceiver(SocketHandler socketHandler, GroupHandler groupHandler, DirectMessages directMessages) throws SocketException {
        this.socket = new DatagramSocket(CommunicationProperties.PORT);
        this.directMessages = directMessages;
        this.socketHandler = socketHandler;
        this.groupHandler = groupHandler;
        pendingGroups = new ConcurrentHashMap<>();
        pendingUsers = new ArrayList<>();
    }

    public void run() {
        stillRunning = true;
        while (stillRunning) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                Message message = CommunicationConverter.fromPacketToMessage(packet);
                if (message.message.startsWith("!")) {
                    handleCommands(message, packet.getAddress().getHostAddress());
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        socket.close();
    }


    private void handleCommands(Message message, String ip) {
        System.out.println("From " + ip);
        if (Objects.equals(message.receiver, CommunicationProperties.MY_NICKNAME)) {
            switch (message.message) {
                case "!hello": {
                    handleHelloCommand(message, ip);
                    return;
                }
                case "!ack": {
                    handleAckCommand(message, ip);
                    return;
                }
                case "!bye": {
                    handleByeMethod(message);
                    return;
                }
//                case "!ackg": {
//                    handleAcknowledgeGroupCommand(message, ip);
//                    return;
//                }
                case "!invite": {
                    handleInviteCommand(message, ip);
                    return;
                }
                case "!stop": {
                    System.out.println(message);
                    stillRunning = false;
                }
            }
        } else {
            if (Objects.equals(message.message, "!ackg")) {
                handleAcknowledgeGroupCommand(message, ip);
            }
        }
    }

    private void handleInviteCommand(Message message, String ip) {
        groupHandler.addNewInvite(message.group, ip);
        socketHandler.addNewIpNickname(ip, message.sender);
        OutputHandler.handleOutput(message.group + " pending connection");
    }

    private void handleAcknowledgeGroupCommand(Message message, String ip) {
        if (groupHandler.removeNicknamesInPending(message.group, message.sender)) {
            OutputHandler.handleOutput("Invite for " + message.sender + " for group " + message.group + " was accepted");
            socketHandler.addNewIpNickname(ip, message.sender);
            groupHandler.addNewMember(message.group, ip);
            try {
                if (socketHandler.getSocketByIp(ip) == null) {
                    System.out.println("Waiting for connection with new member" + ip);
                    Socket clientSocket = socketHandler.acceptNewClient(message.sender, ip);
                    System.out.println("Connected with new member" + ip);
                    directMessages.startNewChat(clientSocket);
                }
                List<String> groupIps = groupHandler.getAllMembers(message.group);
                String myIp = InetAddress.getLocalHost().getHostAddress().trim();
                System.out.println("Starting to send updates");
                for (String memberIp : groupIps) {
                    if (!memberIp.equals(myIp)) {
                        sendGroupUpdate(memberIp, groupIps, message.group);
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println(message.sender + "was not invited to " + message.group);
        }
    }

    private void sendGroupUpdate(String memberIp, List<String> groupIps, String group) throws IOException {
        Socket socket = socketHandler.getSocketByIp(memberIp);
        if (socket == null) {
            System.out.println("No existing socket for " + memberIp);
        } else {
            System.out.println("Sending update to " + memberIp + "for group " + group);
            OutputStream out = socket.getOutputStream();
            Message updateMessage =
                    new Message(CommunicationProperties.MY_NICKNAME, "", "!update", group, groupIps);
            String json = CommunicationConverter.fromMessageToJson(updateMessage);
            out.write(json.getBytes());
        }
    }

    private void handleByeMethod(Message message) {
        Socket socket = socketHandler.getSocketByNickname(message.sender);
        if (socket != null) {
            socketHandler.remove(message.sender);
            OutputHandler.handleOutput(message.sender + " disconnected");
        }
    }

    private void handleAckCommand(Message message, String ip) {
        if (socketHandler.getSocketByIp(ip) == null) {
            try {
                System.out.println(message.sender + " trying to connect");
                Socket clientSocket = socketHandler.acceptNewClient(message.sender, ip);
                OutputHandler.handleOutput(message.sender + " acknowledged connection");
                pendingUsers.remove(message.sender);
                directMessages.startNewChat(clientSocket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println(message.sender + " already connected");
        }

    }

    private void handleHelloCommand(Message message, String ip) {
        pendingUsers.add(message.sender);
        socketHandler.addNewIpNickname(ip, message.sender);
//        System.out.println(message.sender + " pending connection");
        OutputHandler.handleOutput(message.sender + " pending connection");
    }
}
