package io.github.evanmi.distribute.lock.api.util.timer;

public interface Timeout {

    Timer timer();

    TimerTask task();

    boolean isExpired();
    boolean isCancelled();

    boolean cancel();
}
