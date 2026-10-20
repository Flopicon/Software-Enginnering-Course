package edu.itc.se.lab01.task1;

import java.util.Arrays;

public class Main {
    /*
     * start() creates a new thread and runs code concurrently.
     * run() simply executes the method in the current thread, so there is no parallelism.
     * When --sequential is present, we intentionally call run() instead of start() to compare
     * the two behaviors: the countdowns run one after another instead of concurrently.
     */
    public static void main(String[] args) throws InterruptedException {
        boolean sequential = Arrays.asList(args).contains("--sequential");

        Thread apollo = new Countdown("apollo");
        Thread gemini = new Thread(new CountdownTask(), "gemini");
        Thread mercury = new Thread(() -> {CountdownTask Task = new CountdownTask(); Task.run();}, "mercury");
        Thread artemis = Thread.ofVirtual().name("artemis").unstarted(new CountdownTask());

        if (sequential) {
            apollo.run();
            gemini.run();
            mercury.run();
            artemis.run();
        } else {
            apollo.start();
            gemini.start();
            mercury.start();
            artemis.start();
        }

        System.out.println("Mission control: all launched");

        if (!sequential) {
            apollo.join();
            gemini.join();
            mercury.join();
            artemis.join();
        }

        System.out.println("Mission control: all landed");
        printThreadState(apollo);
        printThreadState(gemini);
        printThreadState(mercury);
        printThreadState(artemis);
    }

    private static void printThreadState(Thread thread) {
        System.out.println(thread.getName() + " virtual=" + thread.isVirtual() + ", state=" + thread.getState());
    }
}