package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPCommandSender {
    private final CommunicationConverter communicationConverter = new CommunicationConverter();
    private final DatagramSocket socket;

    public UDPCommandSender() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
    }

    public void sendEcho(String msg) throws IOException {
        DatagramPacket packet = communicationConverter.fromMessageToPacket(msg.trim(), CommunicationProperties.SERVER_IP, CommunicationProperties.PORT);
        socket.send(packet);
    }

    public void close() {
        socket.close();
    }
}
