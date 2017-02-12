package ru.spbau.mit;

import ru.spbau.mit.benchmark.BenchmarkRunner;
import ru.spbau.mit.client.Client;
import ru.spbau.mit.client.TcpClient;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Producer;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;

public class Main {
    private static final BenchmarkRunner br = new BenchmarkRunner(10, new Producer<Client>() {
        @Override
        public Client produce(CreationalContext<Client> ctx) {
            try {
                return new TcpClient(InetAddress.getLocalHost(), 6968, 100, 100, 30);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void dispose(Client instance) {
            try {
                instance.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Set<InjectionPoint> getInjectionPoints() {
            return null;
        }
    });

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        br.start();
    }
}
