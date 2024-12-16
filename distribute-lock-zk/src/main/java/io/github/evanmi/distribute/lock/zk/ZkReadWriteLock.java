package io.github.evanmi.distribute.lock.zk;

import io.github.evanmi.distribute.lock.api.Lock;
import io.github.evanmi.distribute.lock.api.ReadWriteLock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

public class ZkReadWriteLock implements ReadWriteLock {
    private final InterProcessReadWriteLock innerLock;
    public ZkReadWriteLock(String lockPath, CuratorFramework client) {
        this.innerLock = new InterProcessReadWriteLock(client, lockPath);
    }

    @Override
    public Lock readLock() {
        return new ZkLock(innerLock.readLock());
    }

    @Override
    public Lock writeLock() {
        return new ZkLock(innerLock.writeLock());
    }
}
