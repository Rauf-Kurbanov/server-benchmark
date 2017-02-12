package ru.spbau.mit.server.tcp.async.handlers;

import ru.spbau.mit.server.ServerTimestamp;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

//@RequiredArgsConstructor
//public class WriteHandler implements CompletionHandler<Long, ByteBuffer[]> {
public class WriteHandler implements CompletionHandler<Long, Attachement> {
//    private final AsynchronousSocketChannel channel;

    @Override
//    public void completed(Long result, ByteBuffer[] data) {
    public void completed(Long result, Attachement attach) {
        final AsynchronousSocketChannel channel = attach.getClientChanel();
        if (attach.getDataBuffer().hasRemaining()) {
            final ByteBuffer[] data = attach.getData();
            channel.write(data, 0, 2, 0, TimeUnit.NANOSECONDS, attach, this);
            return;
        }
        // TODO ATTACH!
        final ServerTimestamp st = attach.finishClientHandling();
        attach.getServerStatistics().pushStatistics(st);

        final Attachement newAttach = new Attachement(channel, attach.getServerStatistics());
        channel.read(attach.getSizeBuffer(), newAttach, new ReadHandler());
    }

    @Override
//    public void failed(Throwable exc, ByteBuffer[] attachment) {
    public void failed(Throwable exc, Attachement attachment) {
        System.err.println(exc.getMessage());
        exc.printStackTrace();
    }
}