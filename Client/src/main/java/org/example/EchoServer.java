package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class EchoServer {
    private DatagramSocket socket;
    private boolean stillRunning;
    private byte[] buf = new byte[256];
    List<String> connectedUsers = new ArrayList<>();
    ObjectMapper mapper = new ObjectMapper();

    public EchoServer() throws SocketException {
        socket = new DatagramSocket(Main.PORT);
    }

    public void run() throws IOException {
        stillRunning = true;

        while (stillRunning) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            Message message = fromPacketToMessage(packet);
            if (message.getMessage().startsWith("!")) {
                handleCommands(message);
            }
            if(connectedUsers.contains(message.getNickname())) {
                System.out.println(message);
            }

        }
        socket.close();
    }

    private Message fromPacketToMessage(DatagramPacket packet) throws JsonProcessingException {
        String received = new String(packet.getData(), 0, packet.getLength());
        //System.out.println("received: " + received);
        Message message = mapper.readValue(received, Message.class);
        return message;
    }

    private void handleCommands(Message message) throws IOException {
        if (message.getMessage().equals("!hello " + Main.MY_NICKNAME)) {
            Message ackMessage = new Message(Main.MY_NICKNAME, "!ack " + message.getNickname());
            String json = mapper.writeValueAsString(ackMessage);
            buf = json.getBytes();
            DatagramPacket ackPacket
                    = new DatagramPacket(buf, buf.length, InetAddress.getByName(Main.IP), Main.PORT);
            socket.send(ackPacket);
            connectedUsers.add(message.getNickname());
            return;
        }

        if (message.getMessage().equals("!ack " + Main.MY_NICKNAME)) {
            connectedUsers.add(message.getNickname());
            return;
        }

        if (message.getMessage().equals("!bye " + Main.MY_NICKNAME) && connectedUsers.contains(message.getNickname())) {
            connectedUsers.remove(message.getNickname());
        }
    }

}
