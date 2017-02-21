package ru.spbau.mit.server.tcp.async.handlers;

import ru.spbau.mit.server.ServerTimestamp;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

public class WriteHandler implements CompletionHandler<Long, Attachment> {
    @Override
    public void completed(Long result, Attachment attach) {
        final AsynchronousSocketChannel channel = attach.getClientChanel();
        if (attach.getDataBuffer().hasRemaining()) {
            final ByteBuffer[] data = attach.getData();
            channel.write(data, 0, 2, 0, TimeUnit.NANOSECONDS, attach, this);
            return;
        }
        final ServerTimestamp st = attach.finishClientHandling();
        attach.getServerStatistics().pushStatistics(st);

        final Attachment newAttach = new Attachment(channel, attach.getServerStatistics());
        channel.read(attach.getSizeBuffer(), newAttach, new ReadHandler());
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        System.err.println(exc.getMessage());
        exc.printStackTrace();
    }
}