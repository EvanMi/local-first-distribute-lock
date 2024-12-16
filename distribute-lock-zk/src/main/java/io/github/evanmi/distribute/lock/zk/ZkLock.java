package io.github.evanmi.distribute.lock.zk;

import io.github.evanmi.distribute.lock.api.Lock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.TimeUnit;

public class ZkLock implements Lock {
    private final InterProcessMutex innerLock;

    public ZkLock(String lockPath, CuratorFramework client) {
        this.innerLock = new InterProcessMutex(client, lockPath);
    }

    public ZkLock(InterProcessMutex distributeLock) {
        this.innerLock = distributeLock;
    }

    @Override
    public boolean acquire(long nanos) throws Exception {
        return this.innerLock.acquire(nanos, TimeUnit.NANOSECONDS);
    }

    @Override
    public void release() throws Exception {
        this.innerLock.release();
    }
}
