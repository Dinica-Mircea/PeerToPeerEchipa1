package business.directMessages;

import business.GroupHandler;
import business.OutputHandler;
import business.SocketHandler;
import business.ThreadCommon;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//@Component
public class DirectMessages {
    private final ExecutorService executorService;
    private final GroupHandler groupHandler;
    private final SocketHandler socketHandler;
    private final OutputHandler outputHandler;

    public DirectMessages(int numberOfThreads, ThreadCommon threadCommon) {
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
        this.groupHandler = threadCommon.getGroupHandler();
        this.socketHandler = threadCommon.getSocketHandler();
        this.outputHandler = threadCommon.getOutputHandler();
    }

    public void startNewChat(Socket socket) {
        TCPChatReceiver tcpChatReceiver = new TCPChatReceiver(socket, groupHandler, this, socketHandler, outputHandler);
        executorService.submit(tcpChatReceiver);
    }

    public void stop() {
        executorService.shutdown();
    }
}
