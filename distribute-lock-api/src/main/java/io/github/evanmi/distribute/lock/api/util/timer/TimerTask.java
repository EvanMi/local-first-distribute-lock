package io.github.evanmi.distribute.lock.api.util.timer;


public interface TimerTask {
    void run(Timeout timeout) throws Exception;
}
