package webSocket;

import business.ChatApplication;
import business.OutputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private ChatApplication chatApplication;
    @Autowired
    private final OutputHandler outputHandler;

    public WebSocketHandler(OutputHandler outputHandler, ChatApplication chatApplication) {
//        outputHandler = new OutputHandler();
//        try {
//            chatApplication = new ChatApplication(outputHandler);
//        } catch (IOException e) {
//            System.out.println("Couldn't create ChatApplication" + e.getMessage());
//        }
//        System.out.println("Created web socket handler");
        this.outputHandler = outputHandler;
        this.chatApplication = chatApplication;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        chatApplication.sendRequestFromRestService(payload);
    }



    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        outputHandler.add(session);
        session.sendMessage(new TextMessage("!update direct " + chatApplication.getConnectedUsers()));
        session.sendMessage(new TextMessage("!update group " + chatApplication.getGroups()));
    }
}
