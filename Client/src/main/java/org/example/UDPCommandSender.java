package org.example;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

public class UDPCommandSender {
    private final CommunicationConverter communicationConverter = new CommunicationConverter();
    private final DatagramSocket socket;
    private final SocketHandler socketHandler;
    private String currentReceiver;

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
                System.out.println(nickname + " connected");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else if (msg.startsWith("!")) {
            DatagramPacket packet = communicationConverter.fromMessageToPacket(msg.trim(), CommunicationProperties.SERVER_IP, CommunicationProperties.PORT);
            socket.send(packet);
        } else if (msg.startsWith("#")) {
            String nextReceiver = msg.replace("#", "");
            if (socketHandler.getIp(nextReceiver) != null) {
                currentReceiver = nextReceiver;
            } else{
                System.out.println(nextReceiver + " not connected");
            }
        } else {
            Socket socket = socketHandler.getSocket(currentReceiver);
            if (socket == null) {
                System.out.println("No existing ip for " + currentReceiver);
            } else {
                OutputStream out = socket.getOutputStream();
                communicationConverter.fromMessageToJson(CommunicationProperties.MY_NICKNAME, currentReceiver, msg);
                out.write(msg.getBytes());
            }
        }
    }

    public void close() {
        socket.close();
    }
}
