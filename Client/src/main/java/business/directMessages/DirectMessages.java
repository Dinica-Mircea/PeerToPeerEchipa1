package business.directMessages;

import business.GroupHandler;
import business.SocketHandler;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//@Component
public class DirectMessages {
    private final ExecutorService executorService;
    private final GroupHandler groupHandler;
    private final SocketHandler socketHandler;

    public DirectMessages(Integer numberOfThreads, GroupHandler groupHandler, SocketHandler socketHandler) {
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
        this.groupHandler = groupHandler;
        this.socketHandler = socketHandler;
    }

    public void startNewChat(Socket socket) {
        TCPChatReceiver tcpChatReceiver = new TCPChatReceiver(socket, groupHandler, this, socketHandler);
        executorService.submit(tcpChatReceiver);
    }

    public void stop() {
        executorService.shutdown();
    }
}
