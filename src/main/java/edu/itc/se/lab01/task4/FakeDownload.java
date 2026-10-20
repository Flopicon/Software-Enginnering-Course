package edu.itc.se.lab01.task4;
import java.io.IOException;
import java.util.concurrent.Callable;
public final class FakeDownload implements Callable<String> {
    private final int id;
    private final String url;
    private final boolean slow;
    public FakeDownload(int id, String url, boolean slow) {
        this.id = id; this.url = url; this.slow = slow;
    }
    @Override public String call() throws Exception {
        if (id % 50 == 0) throw new IOException("boom");
        Thread.sleep(slow ? 5_000 : 200);
        return url + " -> " + url.length();
    }
}
