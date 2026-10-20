package edu.itc.se.lab01.task4;

import java.util.*;
import java.util.concurrent.*;

public class Main {
    private static final long LIMIT = 4_000_000L;
    private static final int CHUNKS = 16;
    private static final int DOWNLOADS = 500;

    static long countPrimes(long from, long to) {
        long count = 0;
        for (long n = Math.max(from, 2); n < to; n++) {
            boolean prime = true;
            for (long d = 2; d * d <= n; d++) {
                if (n % d == 0) { prime = false; break; }
            }
            if (prime) count++;
        }
        return count;
    }

    private static long elapsedMs(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }

    private static void stopExecutor(ExecutorService executor) throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            executor.shutdownNow();
            if (!executor.awaitTermination(10, TimeUnit.SECONDS))
                System.err.println("Executor did not terminate in time.");
        }
    }

    private static void runPrimeCount() throws Exception {
        long start = System.nanoTime();
        long single = countPrimes(0, LIMIT);
        System.out.printf("single-threaded: %d primes in %d ms%n", single, elapsedMs(start));

        start = System.nanoTime();
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            List<Future<Long>> futures = new ArrayList<>();
            long chunkSize = LIMIT / CHUNKS;
            for (int i = 0; i < CHUNKS; i++) {
                long from = i * chunkSize;
                long to = (i == CHUNKS - 1) ? LIMIT : from + chunkSize;
                futures.add(pool.submit(new PrimeCounter(from, to)));
            }
            long total = 0;
            for (Future<Long> future : futures) total += future.get();
            System.out.printf("pool (%d threads): %d primes in %d ms%n",
                    threads, total, elapsedMs(start));
            if (total != single)
                throw new AssertionError("Parallel count differs from single-threaded count");
        } finally {
            stopExecutor(pool);
        }
    }

    private static void runDownloads(String label, ExecutorService executor)
            throws InterruptedException {
        long start = System.nanoTime();
        List<Future<String>> futures = new ArrayList<>(DOWNLOADS);
        try {
            for (int i = 1; i <= DOWNLOADS; i++) {
                String url = "https://example.com/file-" + i;
                futures.add(executor.submit(new FakeDownload(i, url, i == 1)));
            }

            int ok = 0, failed = 0, timedOut = 0;
            for (Future<String> future : futures) {
                try {
                    future.get(1, TimeUnit.SECONDS);
                    ok++;
                } catch (TimeoutException e) {
                    timedOut++;
                    future.cancel(true);
                } catch (ExecutionException e) {
                    failed++; // Expected simulated IOException("boom"); continue with other tasks.
                } catch (CancellationException e) {
                    failed++;
                }
            }

            System.out.printf("downloads on %s: ok=%d failed=%d timedOut=%d in %d ms%n",
                    label, ok, failed, timedOut, elapsedMs(start));
            if (ok != 489 || failed != 10 || timedOut != 1)
                throw new AssertionError("Unexpected summary: ok=" + ok + ", failed="
                        + failed + ", timedOut=" + timedOut);
        } finally {
            stopExecutor(executor);
        }
    }

    public static void main(String[] args) throws Exception {
        runPrimeCount();
        runDownloads("fixed(20) platform threads", Executors.newFixedThreadPool(20));
        runDownloads("virtual threads", Executors.newVirtualThreadPerTaskExecutor());
    }
}
