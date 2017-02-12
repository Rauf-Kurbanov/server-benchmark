package ru.spbau.mit.server.tcp.async.handlers;

import lombok.RequiredArgsConstructor;
import ru.spbau.mit.server.ServerStatistics;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@RequiredArgsConstructor
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

    private final ServerStatistics serverStatistics;

    @Override
    public void completed(AsynchronousSocketChannel clientSC, AsynchronousServerSocketChannel serverSC) {
        serverSC.accept(serverSC, this);
        final Attachement attach = new Attachement(clientSC, serverStatistics);
        clientSC.read(attach.getSizeBuffer(), attach, new ReadHandler());
    }

    @Override
    public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {

    }
}
