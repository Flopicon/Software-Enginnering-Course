package edu.itc.se.lab01.task2;

public class UnsafeCounter implements Counter {
    private long count = 0;

    @Override
    public void increment() {
        count++; // Not atomic: multiple threads can overwrite each other's updates.
    }

    @Override
    public long get() {
        return count;
    }
}
