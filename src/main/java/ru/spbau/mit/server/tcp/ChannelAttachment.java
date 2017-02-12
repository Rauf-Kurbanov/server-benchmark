package ru.spbau.mit.server.tcp;

import java.nio.ByteBuffer;

//public class ChannelAttachment {
//}

public class ChannelAttachment extends TimeStampingAttachment {
//    private volatile long requestHandlingStart = -1;
//    private volatile long clientHandlingStart = -1;
//    private volatile long requestHandlingDuration;
//    private volatile long clientHandlingDuration;
//
//    public void startRequestHandling() {
//        if (requestHandlingStart == -1) {
//            requestHandlingStart = System.nanoTime();
//        }
//    }
//
//    // TODO check how many times called, I suspect if is redundant
//    public void startClientHandling() {
//        if (clientHandlingStart == -1) {
//            clientHandlingStart = System.nanoTime();
//        }
//    }
//
//    public void finishRequestHandling() {
//        requestHandlingDuration = System.nanoTime() - requestHandlingStart;
//    }
//
//    public ServerTimestamp finishClientHandling() {
//        clientHandlingDuration = System.nanoTime() - clientHandlingStart;
//        return new ServerTimestamp(requestHandlingDuration, clientHandlingDuration);
//    }

    final ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
    ByteBuffer dataBuffer;
    // TODO hwy
    volatile ByteBuffer[] answer;
    volatile NonBlockingServer.ChannelState state = NonBlockingServer.ChannelState.READ_SIZE;
}
