package business;

import business.directMessages.DirectMessages;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

@Component
public class ChatApplication {

    private final UDPCommandReceiver UDPCommandReceiver;
    private final CommandSender commandSender;
    private final OutputHandler outputHandler;

    public ChatApplication() throws IOException {
        System.out.println("Creating socket by chat application");
        outputHandler = new OutputHandler();
        ThreadCommon threadCommon = new ThreadCommon(new GroupHandler(),outputHandler,new SocketHandler());
        DirectMessages directMessages = new DirectMessages(20, threadCommon);
        this.UDPCommandReceiver = new UDPCommandReceiver(threadCommon, directMessages);
        this.commandSender = new CommandSender(threadCommon, directMessages);
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
}
