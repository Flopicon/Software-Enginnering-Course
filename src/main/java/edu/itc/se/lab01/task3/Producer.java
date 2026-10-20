package edu.itc.se.lab01.task3;

import java.util.concurrent.ThreadLocalRandom;

public class Producer implements Runnable {
    private final int producerId;
    private final int ordersToProduce;
    private final OrderQueue queue;
    private final int firstOrderId;

    public Producer(int producerId, int ordersToProduce, int firstOrderId,
                    OrderQueue queue) {
        this.producerId = producerId;
        this.ordersToProduce = ordersToProduce;
        this.firstOrderId = firstOrderId;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < ordersToProduce; i++) {
                Order order = new Order(firstOrderId + i, "item-" + (firstOrderId + i),
                        ThreadLocalRandom.current().nextInt(1, 6));
                Thread.sleep(ThreadLocalRandom.current().nextInt(0, 21));
                queue.put(order);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("producer-" + producerId + " was interrupted");
        }
    }
}
