package io.github.evanmi.distribute.lock.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.evanmi.distribute.lock.api.LockFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public abstract class AbstractLock<T> {
    private final Logger logger = LoggerFactory.getLogger(AbstractLock.class);
    protected final LockFactory lockFactory;
    protected final Cache<String, T> cache;

    public AbstractLock(LocalLockCacheConfig localLockCacheConfig, LockFactory lockFactory) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(localLockCacheConfig.getDuration(), TimeUnit.SECONDS)
                .maximumSize(localLockCacheConfig.getMaximumSize())
                .build();
        this.lockFactory = lockFactory;
    }

    protected T getLocalLockByLockPath(String lockPath) {
        return cache.get(lockPath, this::newLocalLock);
    }

    protected abstract T newLocalLock(String key);


    protected <X> Optional<X> lockSubmit(Lock lock,
                                         io.github.evanmi.distribute.lock.api.Lock distributeLock,
                                         long time, TimeUnit timeUnit,
                                         Callable<X> task) throws InterruptedException {
        long localStartNano = System.nanoTime();
        boolean locked = lock.tryLock(time, timeUnit);
        if (locked) {
            try {
                // 获取锁
                long distributeStartNano = System.nanoTime();
                long nanoTime = time <= 0 ? 0 : timeUnit.toNanos(time) - (distributeStartNano - localStartNano);
                if (nanoTime < 0) {
                    return Optional.empty();
                }
                boolean acquire = distributeLock.acquire(nanoTime);
                if (acquire) {
                    try {
                        return Optional.ofNullable(task.call());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        distributeLock.release();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("local lock timeout");
        }
        return Optional.empty();
    }
}
