package io.github.evanmi.distribute.lock.db.util.timer;

public interface Timeout {

    Timer timer();

    TimerTask task();

    boolean isExpired();
    boolean isCancelled();

    boolean cancel();
}
