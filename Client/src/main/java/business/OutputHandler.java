package business;

import org.springframework.web.socket.TextMessage;
import webSocket.WebSocketHandler;

import java.io.IOException;

public class OutputHandler {
    public static void handleOutput(String output) {
        System.out.println(output);
        try {
            WebSocketHandler.getSession().sendMessage(new TextMessage(output));
        } catch (IOException e) {
            System.out.println("Can't write in web socket " + output);
        }
    }
}
