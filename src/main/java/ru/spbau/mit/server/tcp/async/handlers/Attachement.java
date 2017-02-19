package ru.spbau.mit.server.tcp.async.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.spbau.mit.server.ServerStatistics;
import ru.spbau.mit.server.tcp.TimeStampingAttachment;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

import static ru.spbau.mit.server.tcp.async.handlers.Attachement.State.READ_SIZE;

// TODO consider refactoring
@RequiredArgsConstructor
public class Attachement extends TimeStampingAttachment {
    @Getter
    private final AsynchronousSocketChannel clientChanel;
    @Getter
    private final ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
    @Getter
    private final ServerStatistics serverStatistics;

    @Getter
    @Setter
    private ByteBuffer dataBuffer;
    @Getter
    @Setter
    private State state = READ_SIZE;

    @Setter
    @Getter
    private ByteBuffer[] data;

    enum State {
        READ_SIZE,
        READ_DATA
    }

    public boolean hasRemaining() {
        if (state.equals(READ_SIZE)) {
            return sizeBuffer.hasRemaining();
        }
        return dataBuffer.hasRemaining();
    }

    public ByteBuffer getBuffer() {
        if (state.equals(READ_SIZE)) {
            return sizeBuffer;
        }
        return dataBuffer;
    }
}
