package ru.spbau.mit.client;

import lombok.extern.slf4j.Slf4j;
import ru.spbau.mit.Sorter;
import ru.spbau.mit.benchmark.BenchmarkParameters;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

@Slf4j
public class TcpClient extends Client {

    private final int arraySize;
    private Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    private static int retryTreshold = 3;

    public TcpClient(InetAddress serverAddress, int portNumber
            , int arraySize, int delayInMs, int nQueries) throws IOException, InterruptedException {
        this.arraySize = arraySize;
        this.delayInMs = delayInMs;
        this.nQueries = nQueries;

        int retryCnt = 0;
        while (retryCnt++ < retryTreshold && socket == null) {
            try {
                socket = new Socket(serverAddress, portNumber);
            } catch (IOException e) {
                log.info(String.format("Failed to connect to address: %s, on port %s", serverAddress, portNumber));
                log.info(String.format("try %2d/%2d", retryCnt, retryTreshold));
                Thread.sleep(500);
            }
        }
        // still getting an exception
        if (socket == null) {
            socket = new Socket(serverAddress, portNumber);
        }

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public TcpClient(InetAddress serverAddress, int portNumber, BenchmarkParameters bp) throws IOException, InterruptedException {
        this(serverAddress, portNumber, bp.getArraySize(), bp.getDelayInMs(), bp.getNQueries());
    }

    // TODO why sending array and receiving list?
    @Override
    public List<Integer> askToSort() throws IOException {
        final int[] arrToSort = Sorter.generateArr(arraySize);
        Protocol.sendSortRequest(out, arrToSort);
        final List<Integer> res = Protocol.getSortResponse(in);
        return res;
    }

    @Override
    public void stop() throws IOException {
        socket.close();
    }
}

