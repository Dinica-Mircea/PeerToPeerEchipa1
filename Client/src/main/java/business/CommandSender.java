package business;

import business.directMessages.TCPChatReceiver;
import utils.CommunicationConverter;
import utils.CommunicationProperties;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;

public class CommandSender {
    private final DatagramSocket udpSocket;
    private final SocketHandler socketHandler;
    private String currentReceiver;

    public CommandSender(SocketHandler socketHandler) throws SocketException {
        this.socketHandler = socketHandler;
        this.udpSocket = new DatagramSocket();
    }

    public void sendEcho(String msg) throws IOException {
        if (msg.startsWith("!")) {
            sendCommand(msg);
        } else if (msg.startsWith("#")) {
            updateCurrentReceiver(msg);
        } else {
            sendDirectMessage(msg);
        }
    }

    private void sendDirectMessage(String msg) throws IOException {
        Socket socket = socketHandler.getSocket(currentReceiver);
        if (socket == null) {
            System.out.println("No existing ip for " + currentReceiver);
        } else {
            OutputStream out = socket.getOutputStream();
            String json = CommunicationConverter.fromMessageToJson(currentReceiver, msg);
            out.write(json.getBytes());
        }
    }

    private void updateCurrentReceiver(String msg) {
        String nextReceiver = msg.replace("#", "");
        if (socketHandler.getIp(nextReceiver) != null) {
            currentReceiver = nextReceiver;
            System.out.println("current receiver updated: " + currentReceiver);
        } else {
            System.out.println(nextReceiver + " not connected");
        }
    }

    private void sendAck(String nickname, String command) {
        Socket clientSocket;
        try {
            System.out.println(nickname + "trying to connect");
            DatagramPacket packet = CommunicationConverter.fromMessageToPacket(command, nickname, CommunicationProperties.SERVER_IP, CommunicationProperties.PORT);
            udpSocket.send(packet);
            clientSocket = new Socket(socketHandler.getIp(nickname), CommunicationProperties.PORT);
            socketHandler.addNewSocketIp(clientSocket, socketHandler.getIp(nickname));
            System.out.println(nickname + " connected");
            TCPChatReceiver tcpChatReceiver = new TCPChatReceiver(clientSocket);
            tcpChatReceiver.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendCommand(String msg) {
        String[] split = msg.trim().split(" ");
        String nickname = split[1];
        String command = split[0];
        if (Objects.equals(command, "!ack")) {
            sendAck(nickname, command);
        } else {
            sendUdpMessage(command, nickname);
        }
    }

    private void sendUdpMessage(String command, String nickname) {
        try {
            DatagramPacket packet = CommunicationConverter.fromMessageToPacket(command, nickname, CommunicationProperties.SERVER_IP, CommunicationProperties.PORT);
            udpSocket.send(packet);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void close() {
        udpSocket.close();
    }
}
