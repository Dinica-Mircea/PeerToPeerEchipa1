package org.example.business;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class OutputHandler {
    private final List<WebSocketSession> sessions;

    public OutputHandler() {
        sessions = new ArrayList<>();
    }

    public synchronized void handleOutput(String output) {
        System.out.println(output);
        try {
//            WebSocketHandler.getSession().sendMessage(new TextMessage(output));
            for (WebSocketSession session : sessions) {
                try {
                    session.sendMessage(new TextMessage(output));
                } catch (IOException ex) {
                    System.out.println("Couldn't send message to " + session);
                } catch (IllegalStateException ex) {
                    System.out.println("Closed " + session);
                    try {
                        sessions.remove(session);
                        session.close();
                    } catch (IOException exe) {
                        System.out.println("Couldn't close " + session);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Can't write in web socket " + output);
        }
    }

    public void add(WebSocketSession session) {
        System.out.println("Added new session " + session);
        sessions.add(session);
    }
}