package org.example;

import java.io.IOException;
import java.net.SocketException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
      EchoServer echoServer = new EchoServer();
      echoServer.run();
    }
}