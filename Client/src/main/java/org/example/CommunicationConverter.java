package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class CommunicationConverter {
    ObjectMapper mapper;

    public CommunicationConverter() {
        mapper = new ObjectMapper();
    }

    DatagramPacket fromMessageToPacket(String msg, String IP, Integer PORT) throws UnknownHostException, IncorrectMessageFormatException {
        Message message = new Message(CommunicationProperties.MY_NICKNAME, msg);
        String json;
        try {
            json = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new IncorrectMessageFormatException(msg);
        }
        byte[] buffer = json.getBytes();
        return new DatagramPacket(buffer, buffer.length, InetAddress.getByName(IP), PORT);
    }

    String fromMessageToJson(String sender,String receiver,String msg) throws UnknownHostException, IncorrectMessageFormatException {
        Message message = new Message(sender,receiver, msg);
        String json;
        try {
            json = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new IncorrectMessageFormatException(msg);
        }
       // byte[] buffer = json.getBytes();
        return json;
    }

    public Message fromPacketToMessage(DatagramPacket packet) throws JsonProcessingException {
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("received: " + received);
        return mapper.readValue(received, Message.class);
    }
}