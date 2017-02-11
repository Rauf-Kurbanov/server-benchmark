package ru.spbau.mit.client;

//import ru.spbau.mit.server.MultiThreadedServer;

import ru.spbau.mit.Sorter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

//@RequiredArgsConstructor
public class TcpClient extends Client {

//    private final InetAddress serverAddress;
//    private final int portNumber;
    private final int arraySize;
//    private final int delayInMs;
//    private final int nQueries;

    private Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public TcpClient(InetAddress serverAddress, int portNumber
            , int arraySize, int delayInMs, int nQueries) throws IOException {
//        this.serverAddress = serverAddress;
//        this.portNumber = portNumber;
        this.arraySize = arraySize;
        this.delayInMs = delayInMs;
        this.nQueries = nQueries;

        socket = new Socket(serverAddress, portNumber);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    // TODO why sending array and receiving list?
    @Override
    public List<Integer> askToSort() throws IOException {
//        socket = new Socket(serverAddress, portNumber);
//        final DataInputStream in = new DataInputStream(socket.getInputStream());
//        final DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        final int[] arrToSort = Sorter.generateArr(arraySize);
        Protocol.sendSortRequest(out, arrToSort);
        final List<Integer> res = Protocol.getSortResponse(in);
        return res;
    }
//
//    public void run() throws InterruptedException, IOException {
//        for (int i = 0; i < nQueries; i++) {
//            askToSort();
////            final int[] arrToSort = Sorter.generateArr(arraySize);
////            Protocol.sendSortRequest(out, arrToSort);
////            final List<Integer> res = Protocol.getSortResponse(in);
//            TimeUnit.MILLISECONDS.sleep(delayInMs);
//        }
//    }

    // TODO maybe eliminate and use try with resourses instead
    @Override
    public void stop() throws IOException {
//        out.close();
//        in.close();
        socket.close();
    }
}

