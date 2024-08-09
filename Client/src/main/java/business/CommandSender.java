package business;

import business.directMessages.DirectMessages;
import domain.Message;
import utils.CommunicationConverter;
import utils.CommunicationProperties;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandSender {
    private String currentReceiver;
    private final DatagramSocket udpSocket;
    private final SocketHandler socketHandler;
    private final GroupHandler groupHandler;
    private final DirectMessages directMessages;

    public CommandSender(SocketHandler socketHandler, GroupHandler groupHandler, DirectMessages directMessages) throws SocketException {
        this.socketHandler = socketHandler;
        this.udpSocket = new DatagramSocket();
        this.groupHandler = groupHandler;
        this.directMessages = directMessages;
    }

    public void sendMessage(String msg) throws IOException {
        if (msg.startsWith("!")) {
            sendCommand(msg);
        } else if (msg.startsWith("#")) {
            updateCurrentReceiver(msg);
        } else {
            sendDirectMessage(msg);
        }
    }

    private void sendDirectMessage(String msg) throws IOException {
        if (currentReceiver == null) {
            OutputHandler.handleOutput("No receiver selected");
            return;
        }
        Socket socket = socketHandler.getSocketByNickname(currentReceiver);
        if (socket == null) {
            System.out.println("No existing ip for " + currentReceiver);
            OutputHandler.handleOutput("Couldn't connect with " + currentReceiver);
        } else {
            OutputStream out = socket.getOutputStream();
            Message message = new Message(CommunicationProperties.MY_NICKNAME, currentReceiver, msg);
            String json = CommunicationConverter.fromMessageToJson(message);
            out.write(json.getBytes());
        }
    }

    private void updateCurrentReceiver(String msg) {
        String nextReceiver = msg.replace("#", "");
        if (socketHandler.getIpFromNickname(nextReceiver) != null) {
            currentReceiver = nextReceiver;
            OutputHandler.handleOutput("current receiver updated: " + currentReceiver);
        } else {
            OutputHandler.handleOutput(nextReceiver + " not connected");
        }
    }

    private void sendAck(String nickname, String command) {
        Socket clientSocket;
        try {
            System.out.println(nickname + "trying to connect");
            Message ackMessage = new Message(CommunicationProperties.MY_NICKNAME, nickname, command);
            DatagramPacket packet = CommunicationConverter.fromMessageToPacket(ackMessage, CommunicationProperties.SERVER_IP, CommunicationProperties.PORT);
            udpSocket.send(packet);
            if (socketHandler.getSocketByNickname(nickname) == null) {
                clientSocket = new Socket(socketHandler.getIpFromNickname(nickname), CommunicationProperties.PORT);
                socketHandler.addNewSocketIp(clientSocket, socketHandler.getIpFromNickname(nickname));
                OutputHandler.handleOutput(nickname + " connected");
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
                createNewGroup(groupName);
                return;
            }
            case "!invite": {
                String groupName = split[1];
                String personToBeInvited = split[2];
                sendInvite(groupName, personToBeInvited, command);
                return;
            }
            case "!ackg": {
                String groupName = split[1];
                sendAcknowledgeGroup(groupName, command);
                return;
            }
            case "!sendGroup": {
                String groupName = split[1];
                sendGroupMessage(split, groupName);
                return;
            }
            default: {
                String nickname = split[1];
                sendUdpMessage(command, nickname);
            }
        }
    }

    private void sendInvite(String groupName, String personToBeInvited, String command) {
        groupHandler.addNicknameInPendingGroup(groupName, personToBeInvited);
        sendGroupInvite(command, groupName, personToBeInvited);
    }

    private void sendGroupMessage(String[] splitMessage, String groupName) {
        String message = Arrays.stream(splitMessage)
                .skip(2)
                .reduce("", (currentMessage, currentWord) -> currentMessage.concat(" " + currentWord))
                .trim();
        sendMessageGroup(groupName, message);
    }

    private void createNewGroup(String groupName) {
        List<String> groupIps = new ArrayList<>();
        try {
            String myIp = InetAddress.getLocalHost().getHostAddress().trim();
            System.out.println("Creating group with ip: " + myIp);
            OutputHandler.handleOutput("Creating group: " + groupName);
            groupIps.add(myIp);
            groupHandler.addGroup(groupName, groupIps);
        } catch (UnknownHostException e) {
            System.out.println("Can't get my ip.");
            OutputHandler.handleOutput("Couldn't create a group");
        }
    }

    private void sendMessageGroup(String groupName, String message) {
        System.out.println("Sending the message <<" + message + ">> to group " + groupName);
        if (groupHandler.existsGroup(groupName)) {
            try {
                String myIp = InetAddress.getLocalHost().getHostAddress().trim();
                List<String> membersIps = groupHandler.getAllMembers(groupName);
                System.out.println("I'm sending the message to " + membersIps + " from group " + groupName);
                Message messageToBeSend = new Message(CommunicationProperties.MY_NICKNAME, "", message, groupName);
                for (String memberIp : membersIps) {
                    if (!memberIp.equals(myIp)) {
                        System.out.println("Trying to send a message to " + memberIp + " in group " + groupName);
                        Socket socket = socketHandler.getSocketByIp(memberIp);
                        if (socket != null) {
                            System.out.println("Sending <<" + message + ">> to " + socket.getInetAddress().getHostAddress()
                                    + " in group " + groupName);
                            OutputStream out = socket.getOutputStream();
                            String json = CommunicationConverter.fromMessageToJson(messageToBeSend);
                            out.write(json.getBytes());
                        } else {
                            System.out.println("Couldn't find the socket for " + memberIp + " to send the message in group " + groupName);
                        }
                    }
                }
            } catch (IOException e) {
                OutputHandler.handleOutput("Error sending the message <<" + message + ">> to group " + groupName);
            }
        } else {
            OutputHandler.handleOutput("The group " + groupName + " doesn't exist");
        }
    }

    private void sendAcknowledgeGroup(String groupName, String command) {
        System.out.println("Sending ackg to group " + groupName);
        String inviterIp;
        if ((inviterIp = groupHandler.removeReceivedInvite(groupName)) != null) {
            Socket clientSocket;
            try {
                System.out.println(groupName + " trying to connect");
                Message ackgMessage = new Message(CommunicationProperties.MY_NICKNAME, "", command, groupName);
                DatagramPacket packet = CommunicationConverter.fromMessageToPacket(
                        ackgMessage, CommunicationProperties.SERVER_IP, CommunicationProperties.PORT);
                udpSocket.send(packet);
                System.out.println("Sent packet " + packet);
                if (socketHandler.getSocketByIp(inviterIp) == null) {
                    clientSocket = new Socket(inviterIp, CommunicationProperties.PORT);
                    socketHandler.addNewSocketIp(clientSocket, inviterIp);
                    System.out.println("Connected with inviter with ip: " + inviterIp + " for group " + groupName);
                    directMessages.startNewChat(clientSocket);
                } else {
                    System.out.println("Already connected with ip: " + inviterIp);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Didn't have the invitation for group " + groupName);
            OutputHandler.handleOutput("Didn't have the invitation for group " + groupName);
        }
    }

    private void sendGroupInvite(String command, String groupNickname, String receiver) {
        try {
            Message inviteMessage = new Message(CommunicationProperties.MY_NICKNAME, receiver, command, groupNickname);
            DatagramPacket packet = CommunicationConverter.fromMessageToPacket(inviteMessage, CommunicationProperties.SERVER_IP, CommunicationProperties.PORT);
            udpSocket.send(packet);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendUdpMessage(String command, String nickname) {
        try {
            Message newMessage = new Message(CommunicationProperties.MY_NICKNAME, nickname, command);
            DatagramPacket packet = CommunicationConverter.fromMessageToPacket(newMessage, CommunicationProperties.SERVER_IP, CommunicationProperties.PORT);
            udpSocket.send(packet);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void close() {
        udpSocket.close();
    }
}
