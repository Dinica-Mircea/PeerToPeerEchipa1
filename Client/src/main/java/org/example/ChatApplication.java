package org.example;

import java.net.SocketException;
import java.util.Objects;
import java.util.Scanner;

public class ChatApplication {
    public static void runServer() {
        try {
            MessageReceiver messageReceiver = new MessageReceiver();
            messageReceiver.run();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void runClient() {
        try {
            MessageSender echoClient = new MessageSender();
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String message = scanner.nextLine();
                if (Objects.equals(message, "!stop")) {
                    echoClient.sendEcho(message);
                    echoClient.close();
                    break;
                } else {
                    echoClient.sendEcho(message);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
