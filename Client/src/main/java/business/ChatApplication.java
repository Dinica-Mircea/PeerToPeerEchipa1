package business;

import business.directMessages.DirectMessages;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@Component
public class ChatApplication {

    private final UDPCommandReceiver UDPCommandReceiver;
    private final CommandSender commandSender;
    private final OutputHandler outputHandler;
    private final SocketHandler socketHandler;
    private final GroupHandler groupHandler;


    public ChatApplication(OutputHandler outputHandler) throws IOException {
        System.out.println("Creating socket by chat application");
        socketHandler = new SocketHandler();
        groupHandler = new GroupHandler();
        this.outputHandler = outputHandler;
        DirectMessages directMessages = new DirectMessages(20, groupHandler, socketHandler, outputHandler);
        this.UDPCommandReceiver = new UDPCommandReceiver(socketHandler, groupHandler, directMessages, outputHandler);
        this.commandSender = new CommandSender(socketHandler, groupHandler, directMessages, outputHandler);
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
            outputHandler.handleOutput("Couldn't send message: " + message);
        }
    }
    public void addSession(WebSocketSession session) {
        outputHandler.add(session);
    }

    public String getConnectedUsers() {
        return socketHandler.getDirectChatUsers().stream().reduce("", (sub, elem) -> sub.concat(elem) + ",");
    }

    public String getGroups() {
        return groupHandler.getGroupsNames().stream().reduce("", (sub, elem) -> sub.concat(elem) + ",");
    }

}
