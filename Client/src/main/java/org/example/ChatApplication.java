package org.example;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;
import java.util.Scanner;

public class ChatApplication {
    private static SocketHandler socketHandler;

    public ChatApplication() throws IOException {
        socketHandler=new SocketHandler();
    }

    public void runServer() {
        try {
            UDPCommandReceiver UDPCommandReceiver = new UDPCommandReceiver(socketHandler);
            UDPCommandReceiver.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void runClient() {
        try {
            UDPCommandSender echoClient = new UDPCommandSender(socketHandler);
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String message = scanner.nextLine();

                if (Objects.equals(message, "!stop")) {
                    echoClient.sendEcho(message);
                    echoClient.close();
                    break;
                } else {

                    if (message.startsWith("!")) {
                        echoClient.sendEcho(message);
                    }

                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
