package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.*;

public class EchoClient {
    private DatagramSocket socket;
    private InetAddress address;
    ObjectMapper mapper = new ObjectMapper();


    private byte[] buf;

    public EchoClient() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName(Main.IP);
    }

    public void sendEcho(String msg) throws IOException {
        DatagramPacket packet = fromMessageToPacket(msg.trim());
        socket.send(packet);
    }

    private DatagramPacket fromMessageToPacket(String msg) throws JsonProcessingException {
        Message message = new Message(Main.MY_NICKNAME, msg);
        String json = mapper.writeValueAsString(message);
        buf = json.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, Main.PORT);
        return packet;
    }

    public void close() {
        socket.close();
    }
}
