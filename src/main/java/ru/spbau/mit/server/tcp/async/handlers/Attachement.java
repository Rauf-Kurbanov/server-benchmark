package ru.spbau.mit.server.tcp.async.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

import static ru.spbau.mit.server.tcp.async.handlers.Attachement.State.READ_SIZE;

@RequiredArgsConstructor
public class Attachement {
    @Getter
    private final AsynchronousSocketChannel clientChanel;
    @Getter
    private final ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
    @Getter
    @Setter
    private ByteBuffer dataBuffer;
    @Getter
    @Setter
    private State state = READ_SIZE;

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
