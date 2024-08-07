package business;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class ChatApplication {
    private static SocketHandler socketHandler;

    public ChatApplication() throws IOException {
        socketHandler = new SocketHandler();
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
            CommandSender echoClient = new CommandSender(socketHandler);
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String message = scanner.nextLine();
                echoClient.sendEcho(message);
                if (Objects.equals(message, "!stop")) {
                    echoClient.close();
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
