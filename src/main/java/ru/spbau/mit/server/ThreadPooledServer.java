package ru.spbau.mit.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.mit.spbau.FlyingDataProtos;
import ru.spbau.mit.Protocol;
import ru.spbau.mit.Sorter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//@Slf4j
@RequiredArgsConstructor
public class ThreadPooledServer {

    private final ExecutorService serverThreadExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService executor = Executors.newCachedThreadPool();
//    private final Protocol protocol;
    private ServerSocket serverSocket;

    @Getter
    private int currentPort;

    public void answerServerQuery(Socket socket) throws IOException {
        final DataInputStream dis = new DataInputStream(socket.getInputStream());
        final DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        final byte request = dis.readByte();
        switch (request) {
            case 1:
                final int serializedSize  = dis.readInt();
                byte[] content = new byte[serializedSize];
                dis.readFully(content);
                final FlyingDataProtos.FlyingData fd = FlyingDataProtos.FlyingData.parseFrom(content);
                final List<Integer> values = fd.getValueList();
                int[] array = values.stream().mapToInt(x->x).toArray();

                Sorter.insertionSort(array);

                dos.writeInt(fd.getSerializedSize());
                List<Integer> list = IntStream.of(array).boxed().collect(Collectors.toList());
                FlyingDataProtos.FlyingData newFd = FlyingDataProtos.FlyingData
                        .newBuilder()
                        .addAllValue(list)
                        .build();

                dos.write(newFd.toByteArray());
                break;
        }
    }

//    private synchronized void runServer(int portNumber) throws IOException {
    private synchronized void runServer() throws IOException {
//        log.info("Trying to accept socket");
        serverSocket = new ServerSocket();
        currentPort = serverSocket.getLocalPort();
        while (!serverSocket.isClosed()) {
            try {
//                log.info("Entering serverSocket.accept()");
                final Socket clientSocket = serverSocket.accept();
//                log.info("Exiting serverSocket.accept()");
                executor.execute(() -> {
                    try {
                        answerServerQuery(clientSocket);
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                });
            } catch (IOException e) {
                System.out.println("Cannot open ru.spbau.mit.client socket");
                System.out.println(e.getMessage());
            }
        }
//        log.info("ru.spbau.mit.server main thread stops running");
    }

//    @Override
//public void start(int portNumber) {
    public void start() {
        serverThreadExecutor.execute(() -> {
            try {
//                runServer(portNumber);
                runServer();
            } catch (IOException e) {
                throw new RuntimeException("Can't start ru.spbau.mit.server", e);
            }
        });
    }

//    @Override
    public void stop() {
//        log.info("stopping ru.spbau.mit.server");
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing ru.spbau.mit.server", e);
        }
        executor.shutdownNow();
        serverThreadExecutor.shutdownNow();
    }
}
