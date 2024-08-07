package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.IncorrectMessageFormatException;
import domain.Message;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class CommunicationConverter {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static DatagramPacket fromMessageToPacket(String msg,String receiver, String IP, Integer PORT) throws UnknownHostException, IncorrectMessageFormatException {
        String json = fromMessageToJson(receiver, msg);
        byte[] buffer = json.getBytes();
        return new DatagramPacket(buffer, buffer.length, InetAddress.getByName(IP), PORT);
    }

    public static String fromMessageToJson(String receiver, String msg) throws IncorrectMessageFormatException {
        Message message = new Message(CommunicationProperties.MY_NICKNAME,receiver, msg);
        String json;
        try {
            json = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new IncorrectMessageFormatException(msg);
        }
        return json;
    }

    public static Message fromPacketToMessage(DatagramPacket packet) throws JsonProcessingException {
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("received: " + received.trim());
        return mapper.readValue(received, Message.class);
    }
}