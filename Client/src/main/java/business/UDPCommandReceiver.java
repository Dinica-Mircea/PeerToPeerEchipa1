package business;

import business.directMessages.DirectMessages;
import utils.CommunicationConverter;
import utils.CommunicationProperties;
import domain.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UDPCommandReceiver {
    private final DatagramSocket socket;
    private boolean stillRunning;
    private byte[] buf = new byte[1024];
    List<String> pendingUsers = new ArrayList<>();
    SocketHandler socketHandler;
    DirectMessages directMessages;
    GroupHandler groupHandler;

    public UDPCommandReceiver(SocketHandler socketHandler,GroupHandler groupHandler) throws SocketException {
        this.socket = new DatagramSocket(CommunicationProperties.PORT);
        this.directMessages = new DirectMessages(10);
        this.socketHandler = socketHandler;
        this.groupHandler=groupHandler;
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
                case "!hello":{
                    handleHelloCommand(message, ip);
                    return;
                }
                case "!ack": {
                    handleAckCommand(message, ip);
                    return;
                }
                case "!bye":{
                    handleByeMethod(message);
                    return;
                }
                case "!ackg":{
                    handleAckgCommand(message,ip);
                    return;
                }
                case "!stop":{
                    System.out.println(message);
                    stillRunning = false;
                }
            }
        }
    }

    private void handleAckgCommand(Message message,String ip) {
        if(groupHandler.removeNicknamesInPending(message.group,message.sender)){
            socketHandler.addNewIpNickname(ip,message.sender);
            groupHandler.addNewMember(message.group, ip);
        }
    }

    private void handleByeMethod(Message message) {
        Socket socket=socketHandler.getSocket(message.sender);
        if(socket!=null){
            socketHandler.remove(message.sender);
            System.out.println(message.sender + " disconnected");
        }
    }

    private void handleAckCommand(Message message, String ip) {
        try {
            System.out.println(message.sender + " trying to connect");
            Socket clientSocket = socketHandler.acceptNewClient(message.sender, ip);
            System.out.println(message.sender + " acknowledged connection");
            pendingUsers.remove(message.sender);
            directMessages.startNewChat(clientSocket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleHelloCommand(Message message, String ip) {
        pendingUsers.add(message.sender);
        socketHandler.addNewIpNickname(ip, message.sender);
        System.out.println(message.sender + " pending connection");
    }
}
