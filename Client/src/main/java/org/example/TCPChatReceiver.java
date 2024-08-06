package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.Socket;

public class TCPChatReceiver extends Thread {
    private Socket clientSocket;
    private BufferedReader in;
    private byte[] buf = new byte[1024];
    CommunicationConverter converter = new CommunicationConverter();

    public TCPChatReceiver(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {

            InputStream inputStream = clientSocket.getInputStream();
            while (true) {
                if(inputStream.read(buf)>0){
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    Message message=converter.fromPacketToMessage(packet);
                    System.out.println(message);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
