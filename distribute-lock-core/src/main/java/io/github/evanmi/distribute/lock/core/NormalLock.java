package io.github.evanmi.distribute.lock.core;

import io.github.evanmi.distribute.lock.api.LockFactory;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class NormalLock extends AbstractLock<ReentrantLock> {

    public NormalLock(LocalLockCacheConfig localLockCacheConfig, LockFactory lockFactory) {
        super(localLockCacheConfig, lockFactory);
    }

    @Override
    protected ReentrantLock newLocalLock(String key) {
        return new ReentrantLock();
    }

    public void tryLockExecute(String lockKey, long time, TimeUnit timeUnit, Runnable task) throws InterruptedException {
        tryLockSubmit(lockKey, time, timeUnit, () -> {
            task.run();
            return null;
        });
    }

    public <T> Optional<T> tryLockSubmit(String lockKey, long time, TimeUnit timeUnit, Callable<T> task) throws InterruptedException {
        String lockPath = this.lockFactory.getDistributeLockPath(lockKey);
        ReentrantLock reentrantLock = getLocalLockByLockPath(lockPath);
        return lockSubmit(reentrantLock, this.lockFactory.createLock(lockPath), time, timeUnit, task);
    }
}
