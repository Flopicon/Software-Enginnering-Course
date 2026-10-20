package edu.itc.se.lab01.task3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final int PRODUCER_COUNT = 2;
    private static final int ORDERS_PER_PRODUCER = 50;
    private static final int CONSUMER_COUNT = 3;
    private static final int BUFFER_CAPACITY = 5;

    public static void main(String[] args) throws InterruptedException {
        run("BoundedBuffer", new BoundedBuffer(BUFFER_CAPACITY));
    }

    static void run(String label, OrderQueue queue) throws InterruptedException {
        AtomicInteger processed = new AtomicInteger(0);
        List<Thread> consumers = new ArrayList<>();
        List<Thread> producers = new ArrayList<>();
        long start = System.nanoTime();

        for (int i = 1; i <= CONSUMER_COUNT; i++) {
            Thread thread = new Thread(new Consumer(i, queue, processed), "consumer-" + i);
            consumers.add(thread);
            thread.start();
        }

        for (int i = 1; i <= PRODUCER_COUNT; i++) {
            int firstId = (i - 1) * ORDERS_PER_PRODUCER + 1;
            Thread thread = new Thread(new Producer(i, ORDERS_PER_PRODUCER, firstId, queue), "producer-" + i);
            producers.add(thread);
            thread.start();
        }

        for (Thread thread : producers) {
            thread.join();
        }

        // Send poison pills only after BOTH producers finish, so no real orders
        // can be queued behind a poison pill while consumers are stopping.
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            queue.put(new Order(-1, "POISON", 0));
        }

        for (Thread thread : consumers) {
            thread.join();
        }

        long ms = (System.nanoTime() - start) / 1_000_000;
        int total = processed.get();
        System.out.printf("Total processed: %d (expected 100) in %d ms [%s]%n",
                total, ms, label);
        if (total != PRODUCER_COUNT * ORDERS_PER_PRODUCER) {
            throw new AssertionError("Expected 100 processed orders, got " + total);
        }
    }
}
