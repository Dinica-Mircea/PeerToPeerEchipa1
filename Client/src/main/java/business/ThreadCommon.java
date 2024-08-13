package business;

public class ThreadCommon {
    private final GroupHandler groupHandler;
    private final OutputHandler outputHandler;
    private final SocketHandler socketHandler;

    public ThreadCommon(GroupHandler groupHandler, OutputHandler outputHandler, SocketHandler socketHandler) {
        this.groupHandler = groupHandler;
        this.outputHandler = outputHandler;
        this.socketHandler = socketHandler;
    }

    public OutputHandler getOutputHandler() {
        return outputHandler;
    }

    public GroupHandler getGroupHandler() {
        return groupHandler;
    }

    public SocketHandler getSocketHandler() {
        return socketHandler;
    }
}
