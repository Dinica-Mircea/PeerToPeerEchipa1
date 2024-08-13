package business;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OutputHandler {
    private final List<WebSocketSession> sessions;

    public OutputHandler() {
        sessions = new ArrayList<>();
    }

    public synchronized void handleOutput(String output) {
        System.out.println(output);
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(output));
            } catch (IOException e) {
                System.out.println("Couldn't send message to " + session);
            } catch (IllegalStateException e) {
                System.out.println("Closed " + session);
                try {
                    sessions.remove(session);
                    session.close();
                } catch (IOException ex) {
                    System.out.println("Couldn't close " + session);
                }
            }
        }
    }

    public void add(WebSocketSession session) {
        System.out.println("Added new session " + session);
        sessions.add(session);
    }
}
