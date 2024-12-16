package io.github.evanmi.distribute.lock.redisson;

import io.github.evanmi.distribute.lock.api.Lock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

public class RedissonSpinLock implements Lock {
    private final RLock innerLock;
    public RedissonSpinLock(String path, RedissonClient redissonClient) {
        this.innerLock = redissonClient.getSpinLock(path);
    }
    @Override
    public boolean acquire(long nanos) throws Exception {
        return this.innerLock.tryLock(nanos, TimeUnit.NANOSECONDS);
    }

    @Override
    public void release() throws Exception {
        this.innerLock.unlock();
    }
}
