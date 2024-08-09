package business;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import webSocket.WebSocketHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
