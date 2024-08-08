package business.directMessages;

import business.GroupHandler;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class DirectMessages {
    private final ExecutorService executorService;
    private final GroupHandler groupHandler;

    public DirectMessages(Integer numberOfThreads, GroupHandler groupHandler) {
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
        this.groupHandler = groupHandler;
    }

    public void startNewChat(Socket socket) {
        TCPChatReceiver tcpChatReceiver = new TCPChatReceiver(socket,groupHandler);
        executorService.submit(tcpChatReceiver);
    }

    public void stop(){
        executorService.shutdown();
    }
}
