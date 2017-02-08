package ru.spbau.mit.server.tcp.async.handlers;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

    @Override
    public void completed(AsynchronousSocketChannel clientSC, AsynchronousServerSocketChannel serverSC) {
        serverSC.accept(serverSC, this);
        final Attachement attach = new Attachement(clientSC);
        clientSC.read(attach.getSizeBuffer(), attach, new ReadHandler());
    }

    @Override
    public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {

    }
}
