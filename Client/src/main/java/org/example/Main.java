package org.example;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static final String MY_NICKNAME = "echipa1";
    public static final Integer PORT = 3000;
    public static final String IP = "10.4.1.255";

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(Main::runServer);
        sleep(10);
        executorService.submit(Main::runClient);
    }

    private static void runServer() {
        EchoServer echoServer = null;
        try {
            echoServer = new EchoServer();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
        try {
            echoServer.run();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void runClient() {
        EchoClient echoClient = null;
        try {
            echoClient = new EchoClient();
        } catch (SocketException | UnknownHostException e) {
            System.out.println(e.getMessage());
        }
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String message = scanner.nextLine();
            try {
                echoClient.sendEcho(message);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}