package edu.itc.se.lab01.task3;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Runnable {
    private final int consumerId;
    private final OrderQueue queue;
    private final AtomicInteger processed;

    public Consumer(int consumerId, OrderQueue queue, AtomicInteger processed) {
        this.consumerId = consumerId;
        this.queue = queue;
        this.processed = processed;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Order order = queue.take();

                if (order.id() == -1) {
                    System.out.println("consumer-" + consumerId + " received poison pill, stopping");
                    break;
                }

                Thread.sleep(ThreadLocalRandom.current().nextInt(5, 31));
                processed.incrementAndGet();
                System.out.printf("consumer-%d processed Order[id=%d, item=%s, qty=%d]%n",
                        consumerId, order.id(), order.item(), order.qty());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("consumer-" + consumerId + " was interrupted");
        }
    }
}
