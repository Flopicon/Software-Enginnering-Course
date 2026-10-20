package edu.itc.se.lab01.task2;

import java.util.ArrayList;
import java.util.List;

public class Bench {
    static final int THREADS = 8;
    static final int ITERATIONS = 1_000_000;

    static void run(String label, Counter counter) throws InterruptedException {
        long start = System.nanoTime();
        List<Thread> threads = new ArrayList<>();

        for (int t = 0; t < THREADS; t++) {
            Thread thread = new Thread(() -> {
                for (int i = 0; i < ITERATIONS; i++) {
                    counter.increment();
                }
            }, "inc-" + t);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long ms = (System.nanoTime() - start) / 1_000_000;
        long expected = (long) THREADS * ITERATIONS;
        long value = counter.get();
        System.out.printf("%-22s value=%,d  correct=%-5b %d ms%n",
                label, value, value == expected, ms);
    }

    public static void main(String[] args) throws InterruptedException {
        // Run the unsafe version three times to demonstrate lost updates.
        // Example runs commonly produce values below 8,000,000; the exact values vary.
        // Observed lost-update race: UnsafeCounter produced
        // 7,420,292 instead of 8,000,000 in one test run.
        // Other runs may produce different results.
        run("UnsafeCounter #1", new UnsafeCounter());
        run("UnsafeCounter #2", new UnsafeCounter());
        run("UnsafeCounter #3", new UnsafeCounter());

        run("SynchronizedCounter", new SynchronizedCounter());
        run("LockCounter", new LockCounter());
        run("AtomicCounter", new AtomicCounter());
    }
}
