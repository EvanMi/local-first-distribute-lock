package io.github.evanmi.distribute.lock.core;

import io.github.evanmi.distribute.lock.api.LockFactory;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLock extends AbstractLock<java.util.concurrent.locks.ReadWriteLock> {

    public ReadWriteLock(LocalLockCacheConfig localLockCacheConfig, LockFactory lockFactory) {
        super(localLockCacheConfig, lockFactory);
    }

    @Override
    protected java.util.concurrent.locks.ReadWriteLock newLocalLock(String key) {
        return new ReentrantReadWriteLock();
    }

    public void tryReadLockExecute(String lockKey, long time, TimeUnit timeUnit, Runnable task) throws InterruptedException {
        tryReadLockSubmit(lockKey, time, timeUnit, () -> {
            task.run();
            return null;
        });
    }

    public <T> Optional<T> tryReadLockSubmit(String lockKey, long time, TimeUnit timeUnit, Callable<T> task) throws InterruptedException {
        String lockPath = this.lockFactory.getDistributeLockPath(lockKey);
        Lock readWriteLock = getLocalLockByLockPath(lockPath).readLock();
        return lockSubmit(readWriteLock, this.lockFactory.createReadWriteLock(lockPath).readLock(), time, timeUnit, task);
    }


    public void tryWriteLockExecute(String lockKey, long time, TimeUnit timeUnit, Runnable task) throws InterruptedException {
        tryWriteLockSubmit(lockKey, time, timeUnit, () -> {
            task.run();
            return null;
        });
    }

    public <T> Optional<T> tryWriteLockSubmit(String lockKey, long time, TimeUnit timeUnit, Callable<T> task) throws InterruptedException {
        String lockPath = this.lockFactory.getDistributeLockPath(lockKey);
        Lock readWriteLock = getLocalLockByLockPath(lockPath).writeLock();
        return lockSubmit(readWriteLock, this.lockFactory.createReadWriteLock(lockPath).writeLock(), time, timeUnit, task);
    }

}
