package ru.spbau.mit.client;

import ru.mit.spbau.FlyingDataProtos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface UdpProtocol {

    static void sendSortRequest(DatagramSocket socket, int[] arrToSort, DatagramPacket datagramPacket) throws IOException {
        final List<Integer> iterable = IntStream.of(arrToSort).boxed().collect(Collectors.toList());
        final FlyingDataProtos.FlyingData fd = FlyingDataProtos.FlyingData
                .newBuilder().addAllValue(iterable).build();

        final ByteBuffer byteBuffer = ByteBuffer.wrap(datagramPacket.getData());
        byteBuffer.putInt(fd.getSerializedSize());
        byteBuffer.put(fd.toByteArray());
        datagramPacket.setData(byteBuffer.array(), 0, byteBuffer.position());

        socket.send(datagramPacket);
    }

    static List<Integer> getSortResponse(DatagramSocket socket, DatagramPacket datagramPacket) throws IOException {
        socket.receive(datagramPacket);
        final ByteBuffer buffer = ByteBuffer.wrap(datagramPacket.getData());
        final int length = buffer.getInt();

        byte[] content = new byte[length];
        buffer.get(content);

        final FlyingDataProtos.FlyingData newFd = FlyingDataProtos.FlyingData.parseFrom(content);
        return newFd.getValueList();
    }
}
