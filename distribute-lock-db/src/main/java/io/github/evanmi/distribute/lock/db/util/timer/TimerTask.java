package io.github.evanmi.distribute.lock.db.util.timer;


public interface TimerTask {
    void run(Timeout timeout) throws Exception;
}
