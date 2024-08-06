package org.example;

import java.net.SocketException;
import java.util.Objects;
import java.util.Scanner;

public class ChatApplication {
    public static void runServer() {
        try {
            UDPCommandReceiver UDPCommandReceiver = new UDPCommandReceiver();
            UDPCommandReceiver.run();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void runClient() {
        try {
            UDPCommandSender echoClient = new UDPCommandSender();
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
