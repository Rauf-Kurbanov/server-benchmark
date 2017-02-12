package ru.spbau.mit.server.tcp.async.handlers;

import com.google.protobuf.InvalidProtocolBufferException;
import ru.spbau.mit.server.RequestAnswerer;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

public class ReadHandler implements CompletionHandler<Integer, Attachement> {

    private final RequestAnswerer requestAnswerer = new RequestAnswerer();

    @Override
    public void completed(Integer result, Attachement attach) {
        if (result == -1) {
            return;
        }
        if (result > 0) {
            attach.startClientHandling();
        }
        if (attach.hasRemaining()) {
            if (attach.getClientChanel().isOpen()) {
                attach.getClientChanel().read(attach.getBuffer(), attach, this);
            }
            return;
        }

        switch (attach.getState()) {
            case READ_SIZE:
                attach.getSizeBuffer().flip();
                final int dataSize = attach.getSizeBuffer().getInt();
                final ByteBuffer dataBuffer = ByteBuffer.allocate(dataSize);
                attach.setDataBuffer(dataBuffer);
                attach.setState(Attachement.State.READ_DATA);
                attach.getClientChanel().read(dataBuffer, attach, new ReadHandler());
                break;
            case READ_DATA:
                try {
                    attach.startRequestHandling();
                    final ByteBuffer[] data = requestAnswerer.answerInBuffers(attach.getDataBuffer().array());
                    attach.finishRequestHandling();
                    attach.setData(data);

                    attach.getClientChanel().write(data, 0, 2, 0, TimeUnit.NANOSECONDS, attach
                            , new WriteHandler());
                } catch (InvalidProtocolBufferException e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }
                break;
        }

    }

    @Override
    public void failed(Throwable exc, Attachement attachment) {
        System.err.println(exc.getMessage());
        exc.printStackTrace();
    }

}
