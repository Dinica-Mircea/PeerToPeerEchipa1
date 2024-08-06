package org.example;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketHandler {
    Map<String, Socket> nicknameSocketsPair = new ConcurrentHashMap<>();

}
