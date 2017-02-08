package ru.spbau.mit.server.tcp.async.handlers;

import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class WriteHandler implements CompletionHandler<Long, ByteBuffer[]> {
    private final AsynchronousSocketChannel channel;

    @Override
    public void completed(Long result, ByteBuffer[] data) {
        if (data[1].hasRemaining()) {
            channel.write(data, 0, 2, 0, TimeUnit.NANOSECONDS, data, this);
            return;
        }

        final Attachement attach = new Attachement(channel);
        channel.read(attach.getSizeBuffer(), attach, new ReadHandler());
    }

    @Override
    public void failed(Throwable exc, ByteBuffer[] attachment) {
        System.err.println(exc.getMessage());
        exc.printStackTrace();
    }
}