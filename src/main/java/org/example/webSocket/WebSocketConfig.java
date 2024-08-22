package org.example.webSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketHandler webSocketHandler;

    public WebSocketConfig(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
//        System.out.println("Constructor web socket config");
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        System.out.println("registering socket");
        registry.addHandler(webSocketHandler, "/ws").setAllowedOrigins("*");

    }
}