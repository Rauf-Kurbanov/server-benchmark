package ru.spbau.mit.server.tcp;

import lombok.Data;

import java.nio.ByteBuffer;

@Data
public class ChannelAttachment extends TimeStampingAttachment {
    private final ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
    private ByteBuffer dataBuffer;
    private volatile ByteBuffer[] answer;
    private volatile NonBlockingServer.ChannelState state = NonBlockingServer.ChannelState.READ_SIZE;
}
