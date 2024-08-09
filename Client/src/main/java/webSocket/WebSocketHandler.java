package webSocket;

import business.ChatApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private ChatApplication chatApplication;

    public WebSocketHandler() {
        System.out.println("Created web socket handler");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        chatApplication.sendRequestFromRestService(payload);
        System.out.println("echipa1");
    }
}
