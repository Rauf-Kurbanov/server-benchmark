package ru.spbau.mit.server.tcp;

import com.google.protobuf.InvalidProtocolBufferException;
import ru.spbau.mit.Sorter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.mit.spbau.FlyingDataProtos.FlyingData;

// TODO make everything static
// TODO move into protocol
public class RequestAnswerer {

    public FlyingData getAnswer(byte[] content) throws InvalidProtocolBufferException {
        final FlyingData fd = FlyingData.parseFrom(content);
        final List<Integer> values = fd.getValueList();
        int[] array = values.stream().mapToInt(x -> x).toArray();

        Sorter.insertionSort(array);

        final List<Integer> list = IntStream.of(array).boxed().collect(Collectors.toList());
        return FlyingData
                .newBuilder()
                .addAllValue(list)
                .build();
    }

    public void answerServerQuery(DatagramSocket socket) throws IOException {
        final byte[] oldContent = new byte[socket.getReceiveBufferSize()];
        final DatagramPacket recievedPacket = new DatagramPacket(oldContent , socket.getReceiveBufferSize());
        socket.receive(recievedPacket);

        final ByteBuffer buffer = ByteBuffer.wrap(recievedPacket.getData());
        final int length = buffer.getInt();

        final byte[] content = new byte[length];
        buffer.get(content);

        final FlyingData fd = getAnswer(content);

        final byte[] bytes = new byte[fd.getSerializedSize() + 4];
        final DatagramPacket packetToSend = new DatagramPacket(bytes, bytes.length);
        final ByteBuffer wrapper = ByteBuffer.wrap(bytes);

        wrapper.putInt(fd.getSerializedSize());
        wrapper.put(fd.toByteArray());
        packetToSend.setData(wrapper.array());

        ///
        packetToSend.setAddress(recievedPacket.getAddress());
        packetToSend.setPort(recievedPacket.getPort());
        socket.send(packetToSend);
    }

    public void answerServerQuery(Socket socket) throws IOException {
        final DataInputStream dis = new DataInputStream(socket.getInputStream());
        final DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        final int serializedSize = dis.readInt();
        final byte[] content = new byte[serializedSize];
        dis.readFully(content);
        final FlyingData newFd = getAnswer(content);

        dos.writeInt(newFd.getSerializedSize());
        dos.write(newFd.toByteArray());
    }

    public ByteBuffer[] answerInBuffers(byte[] content) throws InvalidProtocolBufferException {
        final FlyingData newFd = getAnswer(content);

        final ByteBuffer sizeBuffer = ByteBuffer.allocate(4).putInt(newFd.getSerializedSize());
        sizeBuffer.flip();
        return new ByteBuffer[]{sizeBuffer, ByteBuffer.wrap(newFd.toByteArray())};
    }
}
