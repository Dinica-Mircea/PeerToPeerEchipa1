package business;

import business.directMessages.DirectMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

@Component
public class ChatApplication {
    @Autowired
    private final SocketHandler socketHandler;
    @Autowired
    private final GroupHandler groupHandler;
    @Autowired
    private final DirectMessages directMessages;


    public ChatApplication() throws IOException {
        socketHandler = new SocketHandler();
        groupHandler = new GroupHandler();
        directMessages = new DirectMessages(10, groupHandler);
    }

    public void runServer() {
        try {
            UDPCommandReceiver UDPCommandReceiver = new UDPCommandReceiver(socketHandler, groupHandler, directMessages);
            UDPCommandReceiver.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void runClient() {
        try {
            CommandSender echoClient = new CommandSender(socketHandler, groupHandler, directMessages);
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

//    public void sendRequestFromRestService(String message){
//        echoClient
//    }

}
