package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class MessageReceiver {
    private final DatagramSocket socket;
    private boolean stillRunning;
    private byte[] buf = new byte[1024];
    List<String> connectedUsers = new ArrayList<>();
    List<String> pendingUsers = new ArrayList<>();
    CommunicationConverter communicationConverter = new CommunicationConverter();


    public MessageReceiver() throws SocketException {
        socket = new DatagramSocket(CommunicationProperties.PORT);
    }

    public void run(){
        stillRunning = true;
        while (stillRunning) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                Message message = communicationConverter.fromPacketToMessage(packet);
                if (message.message.startsWith("!")) {
                    handleCommands(message);
                } else if (connectedUsers.contains(message.nickname)) {
                    System.out.println(message);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        socket.close();
    }


    private void handleCommands(Message message) {
        if (message.message.equals("!hello " + CommunicationProperties.MY_NICKNAME)) {
            pendingUsers.add(message.nickname);
            System.out.println(message.nickname + " pending connection");
            return;
        }

        if (message.message.equals("!ack " + CommunicationProperties.MY_NICKNAME) && pendingUsers.contains(message.nickname)) {
            pendingUsers.remove(message.nickname);
            connectedUsers.add(message.nickname);
            System.out.println(message.nickname + " acknowledged connection");
            return;
        }

        if (message.message.equals("!bye " + CommunicationProperties.MY_NICKNAME) && connectedUsers.contains(message.nickname)) {
            connectedUsers.remove(message.nickname);
            System.out.println(message.nickname + " disconnected");
            return;
        }

        if(message.message.equals("!stop")){
            System.out.println(message);

            stillRunning = false;
        }
    }

}
