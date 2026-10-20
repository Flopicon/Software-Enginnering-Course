package edu.itc.se.lab01.task3;

import java.util.ArrayDeque;

public class BoundedBuffer implements OrderQueue {
    private final ArrayDeque<Order> items = new ArrayDeque<>();
    private final int capacity;

    public BoundedBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
        this.capacity = capacity;
    }

    @Override
    public synchronized void put(Order order) throws InterruptedException {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        while (items.size() == capacity) {
            wait();
        }

        items.addLast(order);
        // The buffer must never exceed its configured capacity.
        assert items.size() <= capacity;
        notifyAll();
    }

    @Override
    public synchronized Order take() throws InterruptedException {
        while (items.isEmpty()) {
            wait();
        }

        Order order = items.removeFirst();
        // take() must never return an item from an empty buffer.
        assert order != null;
        notifyAll();
        return order;
    }
}
