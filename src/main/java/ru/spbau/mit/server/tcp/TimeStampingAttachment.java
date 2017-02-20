package ru.spbau.mit.server.tcp;

import ru.spbau.mit.server.ServerTimestamp;

public class TimeStampingAttachment {
    private volatile long requestHandlingStart = -1;
    private volatile long clientHandlingStart = -1;
    private volatile long requestHandlingDuration;
    private volatile long clientHandlingDuration;

    public void startRequestHandling() {
        if (requestHandlingStart == -1) {
            requestHandlingStart = System.nanoTime();
        }
    }

    public void startClientHandling() {
        if (clientHandlingStart == -1) {
            clientHandlingStart = System.nanoTime();
        }
    }

    public void finishRequestHandling() {
        requestHandlingDuration = System.nanoTime() - requestHandlingStart;
    }

    public ServerTimestamp finishClientHandling() {
        clientHandlingDuration = System.nanoTime() - clientHandlingStart;
        return new ServerTimestamp(requestHandlingDuration, clientHandlingDuration);
    }
}
