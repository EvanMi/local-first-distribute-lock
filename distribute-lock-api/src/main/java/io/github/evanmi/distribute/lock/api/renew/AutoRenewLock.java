package io.github.evanmi.distribute.lock.api.renew;


import io.github.evanmi.distribute.lock.api.util.timer.ExpirationEntry;
import io.github.evanmi.distribute.lock.api.util.timer.HashedWheelTimer;
import io.github.evanmi.distribute.lock.api.util.timer.Timeout;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public abstract class AutoRenewLock {
    private static final ConcurrentMap<String, ExpirationEntry> EXPIRATION_RENEWAL_MAP = new ConcurrentHashMap<>();
    private final HashedWheelTimer timer = new HashedWheelTimer(100, TimeUnit.MILLISECONDS, 1024);
    private final ThreadLocal<String> threadId = new ThreadLocal<>();
    protected final Long lockLeaseMills;

    protected AutoRenewLock(Long lockLeaseMills) {
        if (null == lockLeaseMills || lockLeaseMills < 1000) {
            lockLeaseMills = 1000L;
        }
        this.lockLeaseMills = lockLeaseMills;
    }

    protected void scheduleExpirationRenewal(String threadId, String lockPath) {
        ExpirationEntry entry = new ExpirationEntry();
        entry.addThreadId(threadId);
        ExpirationEntry oldEntry = EXPIRATION_RENEWAL_MAP.putIfAbsent(getEntryName(lockPath), entry);
        if (oldEntry != null) {
            oldEntry.addThreadId(threadId);
        } else {
            try {
                renewExpiration(lockPath);
            } finally {
                if (Thread.currentThread().isInterrupted()) {
                    cancelExpirationRenewal(threadId, null);
                }
            }
        }
    }

    protected void cancelExpirationRenewal(String threadId, String lockPath) {
        ExpirationEntry task = EXPIRATION_RENEWAL_MAP.get(getEntryName(lockPath));
        if (task == null) {
            return;
        }

        if (threadId != null) {
            task.removeThreadId(threadId);
        }

        if (threadId == null || task.hasNoThreads()) {
            Timeout timeout = task.getTimeout();
            if (timeout != null) {
                timeout.cancel();
            }
            EXPIRATION_RENEWAL_MAP.remove(getEntryName(lockPath));
        }
    }

    private void renewExpiration(String lockPath) {
        ExpirationEntry ee = EXPIRATION_RENEWAL_MAP.get(getEntryName(lockPath));
        if (ee == null) {
            return;
        }

        Timeout task = timer.newTimeout(timeout -> {
            ExpirationEntry ent = EXPIRATION_RENEWAL_MAP.get(getEntryName(lockPath));
            if (ent == null) {
                return;
            }
            String threadId = ent.getFirstThreadId();
            if (threadId == null) {
                return;
            }

            CompletionStage<Boolean> future = renewExpirationAsync(threadId);
            future.whenComplete((res, e) -> {
                if (e != null) {
                    EXPIRATION_RENEWAL_MAP.remove(getEntryName(lockPath));
                    return;
                }
                if (res) {
                    renewExpiration(lockPath);
                }
            });
        }, lockLeaseMills / 3, TimeUnit.MILLISECONDS);

        ee.setTimeout(task);
    }

    protected abstract CompletionStage<Boolean> renewExpirationAsync(String threadId);

    private String getEntryName(String lockPath) {
        return lockPath;
    }

    protected String getThreadId() {
        if (Objects.isNull(threadId.get())) {
            threadId.set(UUID.randomUUID().toString());
        }
        return threadId.get();
    }
}
