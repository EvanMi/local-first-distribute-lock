package io.github.evanmi.distribute.lock.redisson;

import io.github.evanmi.distribute.lock.api.Lock;
import io.github.evanmi.distribute.lock.api.ReadWriteLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

public class RedissonReadWriteLock implements ReadWriteLock {
    private final RReadWriteLock readWriteLock;
    public RedissonReadWriteLock(String path, RedissonClient redissonClient) {
       this.readWriteLock  = redissonClient.getReadWriteLock(path);
    }

    @Override
    public Lock readLock() {
        return new RedissonLock(this.readWriteLock.readLock());
    }

    @Override
    public Lock writeLock() {
        return new RedissonLock(this.readWriteLock.writeLock());
    }
}
