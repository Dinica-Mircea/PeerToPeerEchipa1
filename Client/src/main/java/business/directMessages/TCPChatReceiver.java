package business.directMessages;

import business.GroupHandler;
import business.SocketHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.Message;
import utils.CommunicationConverter;
import utils.CommunicationProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPChatReceiver extends Thread {
    private final Socket clientSocket;
    private BufferedReader in;
    private byte[] buf = new byte[1024];
    GroupHandler groupHandler;
    SocketHandler socketHandler;
    DirectMessages directMessages;

    public TCPChatReceiver(Socket clientSocket, GroupHandler groupHandler,DirectMessages directMessages) {
        this.groupHandler = groupHandler;
        this.clientSocket = clientSocket;
        this.directMessages = directMessages;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            while (true) {
                if (inputStream.read(buf) > 0) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    try {
                        Message message = CommunicationConverter.fromPacketToMessage(packet);
                        System.out.println(message);
                        if (message.message.equals("!update")) {
                            handleUpdateCommand(message);
                        }
                    } catch (JsonProcessingException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private void handleUpdateCommand(Message message) {
        if (groupHandler.existsGroup(message.group)) {
            System.out.println("I am already member of group " + message.group);
            groupHandler.setGroupMembers(message.group, message.ips);
            try {
                String myIp = InetAddress.getLocalHost().getHostAddress().trim(); System.out.println("Starting to initialize connections with group members");
                for (String memberIp : message.ips) {
                    if (!memberIp.equals(myIp)) {
                        if (socketHandler.getSocketByIp(memberIp) != null) {
                            Socket groupMemberSocket = socketHandler.acceptNewClient(memberIp);
                            socketHandler.addNewSocketIp(groupMemberSocket, memberIp);
                            System.out.println("Connected with member of group" + message.group + " with ip: " + memberIp);
                            directMessages.startNewChat(clientSocket);
                        }
                    }
                }

            } catch (UnknownHostException e) {
                System.out.println("can't get host address");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        } else {
            System.out.println("I am new member to group " + message.group);
            groupHandler.addGroup(message.group, message.ips);
            try {
                String myIp = InetAddress.getLocalHost().getHostAddress().trim();
                System.out.println("Starting to initialize connections with group members");
                for (String memberIp : message.ips) {
                    if (!memberIp.equals(myIp) && socketHandler.getSocketByIp(memberIp) != null) {
                            Socket groupMemberSocket = new Socket(memberIp, CommunicationProperties.PORT);
                            socketHandler.addNewSocketIp(groupMemberSocket, memberIp);
                            System.out.println("Connected with member of group" + message.group + " with ip: " + memberIp);
                            directMessages.startNewChat(clientSocket);
                    }
                }
            } catch (UnknownHostException e) {
                System.out.println("can't get host address");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(message.ips);
    }
}
