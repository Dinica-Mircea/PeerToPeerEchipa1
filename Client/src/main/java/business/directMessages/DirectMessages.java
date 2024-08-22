package business.directMessages;

import business.GroupHandler;
import business.OutputHandler;
import business.SocketHandler;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//@Component
public class DirectMessages {
    private final ExecutorService executorService;
    private final GroupHandler groupHandler;
    private final SocketHandler socketHandler;
    private final OutputHandler outputHandler;


    public DirectMessages(Integer numberOfThreads, GroupHandler groupHandler, SocketHandler socketHandler, OutputHandler outputHandler) {
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
        this.groupHandler = groupHandler;
        this.socketHandler = socketHandler;
        this.outputHandler = outputHandler;
    }

    public void startNewChat(Socket socket) {
        TCPChatReceiver tcpChatReceiver = new TCPChatReceiver(socket, groupHandler, this, socketHandler, this.outputHandler);
        executorService.submit(tcpChatReceiver);
    }

    public void stop() {
        executorService.shutdown();
    }
}
