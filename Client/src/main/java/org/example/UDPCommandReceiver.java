package org.example;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class UDPCommandReceiver {
    private final DatagramSocket socket;
    private boolean stillRunning;
    private byte[] buf = new byte[1024];
    List<String> connectedUsers = new ArrayList<>();
    List<String> pendingUsers = new ArrayList<>();
    CommunicationConverter communicationConverter = new CommunicationConverter();
    SocketHandler socketHandler;

    public UDPCommandReceiver(SocketHandler socketHandler) throws SocketException {
        socket = new DatagramSocket(CommunicationProperties.PORT);
        this.socketHandler=socketHandler;
    }

    public void run() {
        stillRunning = true;
        while (stillRunning) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                Message message = communicationConverter.fromPacketToMessage(packet);
                if (message.message.startsWith("!")) {
                    handleCommands(message, packet.getAddress().getHostAddress());
                } else if (connectedUsers.contains(message.sender)) {
                    System.out.println(message);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        socket.close();
    }


    private void handleCommands(Message message, String ip) {
        System.out.println("From " + ip.toString());
        if (message.message.equals("!hello " + CommunicationProperties.MY_NICKNAME)) {
            pendingUsers.add(message.sender);
//            try {
//                Socket clientSocket = new Socket(ip, CommunicationProperties.PORT);
//                TCPChatReceiver tcpChatReceiver = new TCPChatReceiver(clientSocket);
//                tcpChatReceiver.start();
//            } catch (IOException e) {
//                System.out.println(e.getMessage());
//            }

            System.out.println(message.sender + " pending connection");
            return;
        }

        if (message.message.equals("!ack " + CommunicationProperties.MY_NICKNAME) && pendingUsers.contains(message.sender)) {
            try {
                System.out.println(message.sender + " trying to connect");
                Socket clientSocket = socketHandler.acceptNewClient();
                socketHandler.addNewConnection(clientSocket,message.sender);
                System.out.println(message.sender + " acknowledged connection");
                pendingUsers.remove(message.sender);
                connectedUsers.add(message.sender);
                TCPChatReceiver tcpChatReceiver = new TCPChatReceiver(clientSocket);
                tcpChatReceiver.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        if (message.message.equals("!bye " + CommunicationProperties.MY_NICKNAME) && connectedUsers.contains(message.sender)) {
            connectedUsers.remove(message.sender);
            System.out.println(message.sender + " disconnected");
            return;
        }

        if (message.message.equals("!stop")) {
            System.out.println(message);

            stillRunning = false;
        }
    }

}
