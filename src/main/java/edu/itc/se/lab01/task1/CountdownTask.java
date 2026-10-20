package edu.itc.se.lab01.task1;

public class CountdownTask implements Runnable {
    @Override
    public void run() {
        Countdown.countdown();
    }
}