package business.directMessages;

import business.GroupHandler;
import business.SocketHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.Message;
import utils.CommunicationConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.Objects;

public class TCPChatReceiver extends Thread {
    private final Socket clientSocket;
    private BufferedReader in;
    private byte[] buf = new byte[1024];
    GroupHandler groupHandler;
    SocketHandler socketHandler;

    public TCPChatReceiver(Socket clientSocket, GroupHandler groupHandler) {
        this.groupHandler = groupHandler;
        this.clientSocket = clientSocket;
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
                        if(message.message.equals("!update")){
                            handleUpdateCommmand(message);
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

    private void handleUpdateCommmand(Message message) {
        if (groupHandler.existsGroup(message.group)) {
            System.out.println("Group " + message.group + " already exists");
        }
        else {
            System.out.println("Group " + message.group + " doesn't exists");
        }
        System.out.println(message.ips);
    }
}
