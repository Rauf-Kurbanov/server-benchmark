package ru.spbau.mit.client;

import ru.spbau.mit.Sorter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

//        byte[] data = wrapper.array();
//
//        if (data.length > MAX_DATAGRAM_LENGTH) {
//            throw new Exception("Maximum UDP data length exceeded");
//        }
//@RequiredArgsConstructor
public class UdpClient extends Client {

    // TODO maybe get rid ot these
    private final InetAddress serverAddress;
    private final int portNumber;
    private final int arraySize;
//    private final int delayInMs;
//    private final int nQueries;
    private DatagramSocket socket;

    public UdpClient(InetAddress serverAddress, int portNumber
            , int arraySize, int delayInMs, int nQueries) throws IOException {
        this.serverAddress = serverAddress;
        this.portNumber = portNumber;
        this.arraySize = arraySize;
        this.delayInMs = delayInMs;
        this.nQueries = nQueries;

        socket = new DatagramSocket();
        socket.setSoTimeout((int) TimeUnit.SECONDS.toMillis(30));
    }

    @Override
    public List<Integer> askToSort() throws IOException {
//        socket = new DatagramSocket();
        // TODO why
        final int[] arrToSort = Sorter.generateArr(arraySize);

        final byte[] buffer = new byte[1 << 16];

        final DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length,
                serverAddress, portNumber);
        UdpProtocol.sendSortRequest(socket, arrToSort, datagramPacket);
        final List<Integer> res = UdpProtocol.getSortResponse(socket, datagramPacket);
        return res;
//        try (final DatagramSocket socket = new DatagramSocket()) {
//
//        }
    }

    @Override
    public void stop() throws IOException {
        socket.close();
    }
}
