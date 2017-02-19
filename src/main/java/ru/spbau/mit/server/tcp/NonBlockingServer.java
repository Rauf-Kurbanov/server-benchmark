package ru.spbau.mit.server.tcp;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import ru.spbau.mit.server.RequestAnswerer;
import ru.spbau.mit.server.Server;
import ru.spbau.mit.server.ServerTimestamp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class NonBlockingServer extends Server {

    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() + 1;
    private final ExecutorService myThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private Selector selector;
    private volatile ServerSocketChannel serverSocketChannel;
    private final List<SocketChannel> activeChannels = new CopyOnWriteArrayList<>();

    private final RequestAnswerer requestAnswerer = new RequestAnswerer();

    @Override
    protected void runServer(int portNumber) throws IOException {
        selector = Selector.open();

        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(portNumber), Integer.MAX_VALUE);
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (serverSocketChannel.isOpen()) {
            selector.select();
            final Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                final SelectionKey key = keyIterator.next();
                keyIterator.remove();

                if (key.isValid() && key.isAcceptable()) {
                    accept(key);
                }
                if (key.isValid() && key.isReadable()) {
                    read(key);
                }
                if (key.isValid() && key.isWritable()) {
                    write(key);
                }
            }
        }
    }

    private void read(SelectionKey key) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();
        checkContext(key);

        final ChannelAttachment context = (ChannelAttachment) key.attachment();
        switch (context.getState()) {
            case READ_SIZE:
                // TODO why do you close chanel here but assume it closes itself in async server
                final int readCount = channel.read(context.getSizeBuffer());
                if (readCount == -1) {
                    key.cancel();
                    channel.close();
                }
                if (readCount > 0) {
                    context.startClientHandling();
                }
                if (context.getSizeBuffer().hasRemaining()) {
                    break;
                }
                context.getSizeBuffer().flip();
                final int size = context.getSizeBuffer().getInt();
                context.setDataBuffer(ByteBuffer.allocate(size));
                context.setState(ChannelState.READ_DATA);
            case READ_DATA:
                channel.read(context.getDataBuffer());
                if (context.getDataBuffer().hasRemaining()) {
                    break;
                }
                context.setState(ChannelState.PROCEED);
                myThreadPool.execute(() -> {
                    context.startRequestHandling();
                    final byte[] data = (byte[]) context.getDataBuffer().flip().array();
                    try {
                        context.setAnswer(requestAnswerer.answerInBuffers(data));
                        context.finishRequestHandling();
                        context.setState(ChannelState.WRITE);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                });
            default:
                break;
        }
    }

    private void checkContext(SelectionKey key) {
        final ChannelAttachment context = (ChannelAttachment) key.attachment();
        if (context == null || context.getState() == ChannelState.DONE) {
            final ChannelAttachment newContext = new ChannelAttachment();
            key.attach(newContext);
        }
    }

    private void write(SelectionKey key) throws IOException {
        final ChannelAttachment context = (ChannelAttachment) key.attachment();
        if (context.getState() == ChannelState.WRITE) {
            final SocketChannel channel = (SocketChannel) key.channel();
            if (context.getAnswer()[1].hasRemaining()) {
                channel.write(context.getAnswer());
            }

            if (!context.getAnswer()[1].hasRemaining()) {
                final ServerTimestamp st = context.finishClientHandling();
                serverStatistics.pushStatistics(st);
                context.setState(ChannelState.DONE);
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        final SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
        channel.configureBlocking(false);
        channel.socket().setTcpNoDelay(true);
        final ChannelAttachment context = new ChannelAttachment();
        channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE,
                context);
        activeChannels.add(channel);
    }

    @Override
    public void shutdown() throws IOException {
        super.shutdown();
        if (serverSocketChannel != null) {
            serverSocketChannel.close();
            serverSocketChannel.socket().close();
        }
        for (SocketChannel channel : activeChannels) {
            channel.close();
        }
    }

    public enum ChannelState {
        READ_SIZE, READ_DATA, PROCEED, WRITE, DONE
    }
}
