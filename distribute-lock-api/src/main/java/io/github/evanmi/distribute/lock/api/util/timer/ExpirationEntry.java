package io.github.evanmi.distribute.lock.api.util.timer;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ExpirationEntry {

    private final Queue<String> threadsQueue = new ConcurrentLinkedQueue<>();
    private final Map<String, Integer> threadIds = new ConcurrentHashMap<>();
    private volatile Timeout timeout;

    public ExpirationEntry() {
        super();
    }

    public void addThreadId(String threadId) {
        threadIds.compute(threadId, (t, counter) -> {
            counter = Optional.ofNullable(counter).orElse(0);
            counter++;
            threadsQueue.add(threadId);
            return counter;
        });
    }

    public boolean hasNoThreads() {
        return threadsQueue.isEmpty();
    }

    public String getFirstThreadId() {
        return threadsQueue.peek();
    }

    public void removeThreadId(String threadId) {
        threadIds.computeIfPresent(threadId, (t, counter) -> {
            counter--;
            if (counter == 0) {
                threadsQueue.remove(threadId);
                return null;
            }
            return counter;
        });
    }

    public void setTimeout(Timeout timeout) {
        this.timeout = timeout;
    }
    public Timeout getTimeout() {
        return timeout;
    }

}
