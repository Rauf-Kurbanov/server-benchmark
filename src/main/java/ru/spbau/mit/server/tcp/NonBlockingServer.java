package ru.spbau.mit.server.tcp;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import ru.spbau.mit.server.RequestAnswerer;
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

//    private final ExecutorService serverThreadExecutor = Executors.newSingleThreadExecutor();
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

                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    log.info("accepting " + key);
                    accept(key);
                }
                if (key.isReadable()) {
                    log.info("reading " + key);
                    read(key);
                }
                if (key.isWritable()) {
                    log.info("writing " + key);
                    write(key);
                }
            }
        }
    }

    private void read(SelectionKey key) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();
        checkContext(key);

        final ChannelAttachment context = (ChannelAttachment) key.attachment();
        switch (context.state) {
            case READ_SIZE:
                // TODO why do you close chanel here but assume it closes itself in async server
                final int readCount = channel.read(context.sizeBuffer);
                if (readCount == -1) {
                    key.cancel();
                    channel.close();
                }

//                if (readCount > 0 && context.clientHandlingStart == -1) {
                if (readCount > 0) {
                    context.startClientHandling();
//                    context.clientHandlingStart = System.nanoTime(); ////////////////////////////////
                }

                if (context.sizeBuffer.hasRemaining()) {
                    break;
                }

                context.sizeBuffer.flip();
                final int size = context.sizeBuffer.getInt();
                System.out.println("size = " + size);
                context.dataBuffer = ByteBuffer.allocate(size);
                context.state = ChannelState.READ_DATA;
            case READ_DATA:
                channel.read(context.dataBuffer);
                if (context.dataBuffer.hasRemaining()) {
                    break;
                }
                context.state = ChannelState.PROCEED;
                myThreadPool.execute(() -> {
                    context.startRequestHandling();
//                    context.requestHandlingStart = System.nanoTime(); ////////////////////////////////
                    final byte[] data = (byte[]) context.dataBuffer.flip().array();
                    try {
                        // TODO
                        context.answer = requestAnswerer.answerInBuffers(data);
//                        context.requestHandlingDuration = System.nanoTime() - context.requestHandlingStart;
                        context.finishRequestHandling();
                        context.state = ChannelState.WRITE;
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
        if (context == null || context.state == ChannelState.DONE) {
            final ChannelAttachment newContext = new ChannelAttachment();
            key.attach(newContext);
        }
    }

    private void write(SelectionKey key) throws IOException {
        final ChannelAttachment context = (ChannelAttachment) key.attachment();
        if (context.state == ChannelState.WRITE) {
            final SocketChannel channel = (SocketChannel) key.channel();
            if (context.answer[1].hasRemaining()) {
                channel.write(context.answer);
            }

            if (!context.answer[1].hasRemaining()) {
                final ServerTimestamp st = context.finishClientHandling();
                serverStatistics.pushStatistics(st);
//                context.clientHandlingDuration = System.nanoTime() - context.clientHandlingStart;
                // push statistics
                context.state = ChannelState.DONE;
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
    public void stop() throws IOException {
        super.stop();
        if (serverSocketChannel != null) {
            serverSocketChannel.close();
            serverSocketChannel.socket().close();
        }

        for (SocketChannel channel : activeChannels) {
            channel.close();
        }
    }

//    private static class ChannelAttachment {
//        private ServerTimestamp serverTimestamp;
//
//        final ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
//        ByteBuffer dataBuffer;
//        // TODO hwy
//        volatile ByteBuffer[] answer;
//        volatile ChannelState state = ChannelState.READ_SIZE;
//
////        public void startClientHandling() {
////            serverTimestamp.
////        }
//    }

    public enum ChannelState {
        READ_SIZE, READ_DATA, PROCEED, WRITE, DONE
    }
}
