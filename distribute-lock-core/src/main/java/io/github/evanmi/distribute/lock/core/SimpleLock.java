package io.github.evanmi.distribute.lock.core;

import io.github.evanmi.distribute.lock.api.LockFactory;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * In some scenarios, we don't need very complex locks.
 * Specifically, sometimes when obtaining a lock fails, a failure can be immediately returned without any waiting. 
 * In Java, there is a corresponding tryLock method, and many distributed lock implementations mostly 
 * implement the tryLock (long timeout, TimeUnit unit) semantics.
 * To support waiting, the watch is used in zk, 
 * and the event listening is used in Redisson.
 * The most resource consuming way is to continuously loop locally. 
 * The semantics of SimpleLock are based on the semantics of tryLock.
 * Now that the local tryLock is successful, 
 * then perform tryLock on the distributed lock.
 */
public class SimpleLock extends AbstractLock<ReentrantLock>{
    public SimpleLock(LocalLockCacheConfig localLockCacheConfig, LockFactory lockFactory) {
        super(localLockCacheConfig, lockFactory);
    }

    @Override
    protected ReentrantLock newLocalLock(String key) {
        return new ReentrantLock();
    }

    public void tryLockExecute(String lockKey, Runnable task) throws InterruptedException {
        tryLockSubmit(lockKey, () -> {
            task.run();
            return null;
        });
    }

    public <T> Optional<T> tryLockSubmit(String lockKey, Callable<T> task) throws InterruptedException {
        String lockPath = this.lockFactory.getDistributeLockPath(lockKey);
        ReentrantLock reentrantLock = getLocalLockByLockPath(lockPath);
        return lockSubmit(reentrantLock, this.lockFactory.createSimpleLock(lockPath), 0, TimeUnit.NANOSECONDS, task);
    }
}
