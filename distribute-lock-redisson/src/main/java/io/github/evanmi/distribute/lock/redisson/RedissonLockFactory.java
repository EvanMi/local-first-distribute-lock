package io.github.evanmi.distribute.lock.redisson;

import io.github.evanmi.distribute.lock.api.AbstractClientLockFactory;
import io.github.evanmi.distribute.lock.api.Lock;
import io.github.evanmi.distribute.lock.api.ReadWriteLock;
import org.redisson.api.RedissonClient;

import java.util.Objects;

public class RedissonLockFactory extends AbstractClientLockFactory<RedissonClient, RedissonLockConfig> {
    private final Boolean isSpin;

    public RedissonLockFactory(RedissonLockConfig config, RedissonClientListProvider redissonClientListProvider) {
        super(config, redissonClientListProvider.initClients().toArray(new RedissonClient[0]));
        isSpin = Objects.requireNonNull(config.getSpin(), "isSpin argument is required");
    }

    @Override
    public String lockPathSeparator() {
        return ":";
    }

    @Override
    public Lock createLock(String lockPath) {
        return isSpin ? new RedissonSpinLock(lockPath, getClient(lockPath)) :
                new RedissonLock(lockPath, getClient(lockPath));
    }

    @Override
    public ReadWriteLock createReadWriteLock(String lockPath) {
        return new RedissonReadWriteLock(lockPath, getClient(lockPath));
    }
}
