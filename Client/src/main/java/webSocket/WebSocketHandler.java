package webSocket;

import business.ChatApplication;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private ChatApplication chatApplication;
    private static WebSocketSession session;

    public WebSocketHandler() {
        System.out.println("Created web socket handler");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        chatApplication.sendRequestFromRestService(payload);
        System.out.println("echipa1");
        try {
            session.sendMessage(new TextMessage("works"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        this.session = session;
    }

    public static WebSocketSession getSession() throws IOException {
        if (session == null) {
            throw new IOException("No session connected");
        }
        return session;
    }
}
