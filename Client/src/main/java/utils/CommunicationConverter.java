package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.IncorrectMessageFormatException;
import domain.Message;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class CommunicationConverter {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static DatagramPacket fromMessageToPacket(Message message, String IP, Integer PORT) throws UnknownHostException, IncorrectMessageFormatException {
        String json = fromMessageToJson(message);
        byte[] buffer = json.getBytes();
        return new DatagramPacket(buffer, buffer.length, InetAddress.getByName(IP), PORT);
    }

    public static Message fromPacketToMessage(DatagramPacket packet) throws JsonProcessingException {
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("received: " + received.trim());
        return mapper.readValue(received, Message.class);
    }

    public static String fromMessageToJson(Message message) throws IncorrectMessageFormatException {
        String json;
        try {
            json = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new IncorrectMessageFormatException(e.getMessage());
        }
        return json;
    }


//        public static String fromMessageToJson(String receiver, String msg) throws IncorrectMessageFormatException {
//        Message message = new Message(CommunicationProperties.MY_NICKNAME,receiver, msg);
//        String json;
//        try {
//            json = mapper.writeValueAsString(message);
//        } catch (JsonProcessingException e) {
//            throw new IncorrectMessageFormatException(msg);
//        }
//        return json;
//    }
//
//    public static DatagramPacket fromMessageGroupToPacket(String msg,String receiver, String group, String IP, Integer PORT) throws UnknownHostException, IncorrectMessageFormatException {
//        String json = fromMessageGroupToJson(receiver, msg, group);
//        byte[] buffer = json.getBytes();
//        return new DatagramPacket(buffer, buffer.length, InetAddress.getByName(IP), PORT);
//    }
//
//    public static String fromMessageGroupToJson(String receiver, String msg, String group) throws IncorrectMessageFormatException {
//        Message message = new Message(CommunicationProperties.MY_NICKNAME,receiver, msg, group);
//        String json;
//        try {
//            json = mapper.writeValueAsString(message);
//        } catch (JsonProcessingException e) {
//            throw new IncorrectMessageFormatException(msg);
//        }
//        return json;
//    }
//
//    public static String fromUpdateMessageToJson(String group, List<String> groupIps) throws IncorrectMessageFormatException {
//        Message message = new Message(CommunicationProperties.MY_NICKNAME,"", "!update", group, groupIps);
//        String json;
//        try {
//            json = mapper.writeValueAsString(message);
//        } catch (JsonProcessingException e) {
//            throw new IncorrectMessageFormatException(e.getMessage());
//        }
//        return json;
//    }
//
//        public static DatagramPacket fromMessageToPacket(String msg,String receiver, String IP, Integer PORT) throws UnknownHostException, IncorrectMessageFormatException {
//        String json = fromMessageToJson(receiver, msg);
//        byte[] buffer = json.getBytes();
//        return new DatagramPacket(buffer, buffer.length, InetAddress.getByName(IP), PORT);
//    }

}