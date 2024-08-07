package business.directMessages;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DirectMessages {
    private final ExecutorService executorService;

    public DirectMessages(Integer numberOfThreads) {
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
    }

    public void startNewChat(Socket socket) {
        TCPChatReceiver tcpChatReceiver = new TCPChatReceiver(socket);
        executorService.submit(tcpChatReceiver);
    }

    public void stop(){
        executorService.shutdown();
    }
}
