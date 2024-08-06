package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

public class UDPCommandSender {
    private final CommunicationConverter communicationConverter = new CommunicationConverter();
    private final DatagramSocket socket;
    private final SocketHandler socketHandler;

    public UDPCommandSender(SocketHandler socketHandler) throws SocketException {
        this.socketHandler = socketHandler;
        socket = new DatagramSocket();
    }

    public void sendEcho(String msg) throws IOException {
        if (msg.startsWith("!ack")) {
            String nickname = msg.replace("!ack ", "");
            Socket clientSocket;
            try {
                System.out.println(nickname + "trying to connect");
                DatagramPacket packet = communicationConverter.fromMessageToPacket(msg.trim(), CommunicationProperties.SERVER_IP, CommunicationProperties.PORT);
                socket.send(packet);
                clientSocket = new Socket(socketHandler.getIp(nickname), CommunicationProperties.PORT);
                socketHandler.addNewConnection(clientSocket, nickname);
                //clientSocket = socketHandler.acceptNewClient();
                System.out.println(nickname + " connected");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            DatagramPacket packet = communicationConverter.fromMessageToPacket(msg.trim(), CommunicationProperties.SERVER_IP, CommunicationProperties.PORT);
            socket.send(packet);
        }

    }

    public void close() {
        socket.close();
    }
}
