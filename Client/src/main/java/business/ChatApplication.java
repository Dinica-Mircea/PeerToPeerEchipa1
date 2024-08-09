package business;

import business.directMessages.DirectMessages;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

@Component
public class ChatApplication {

    UDPCommandReceiver UDPCommandReceiver;
    CommandSender commandSender;

    public ChatApplication() throws IOException {
        System.out.println("Creating socket by chat application");
        SocketHandler socketHandler = new SocketHandler();
        GroupHandler groupHandler = new GroupHandler();
        DirectMessages directMessages = new DirectMessages(20, groupHandler, socketHandler);
        this.UDPCommandReceiver = new UDPCommandReceiver(socketHandler, groupHandler, directMessages);
        this.commandSender = new CommandSender(socketHandler, groupHandler, directMessages);
    }

    public void runServer() {
        try {
            UDPCommandReceiver.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void runClient() {
        try {
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String message = scanner.nextLine();
                commandSender.sendMessage(message);
                if (Objects.equals(message, "!stop")) {
                    commandSender.close();
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendRequestFromRestService(String message) {
        try {
            commandSender.sendMessage(message);
        } catch (IOException e) {
            System.out.println("Couldn't send message: " + message);
        }
    }

}
