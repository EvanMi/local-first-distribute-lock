package io.github.evanmi.distribute.lock.redisson;

import io.github.evanmi.distribute.lock.api.AbstractClientLockFactory;
import io.github.evanmi.distribute.lock.api.Lock;
import io.github.evanmi.distribute.lock.api.ReadWriteLock;
import org.redisson.api.RedissonClient;

import java.util.Objects;

public class RedissonLockFactory extends AbstractClientLockFactory<RedissonClient, RedissonLockConfig> {
    private final Boolean isSpin;
    private final Long lockLeaseMills;

    public RedissonLockFactory(RedissonLockConfig config, RedissonClientListProvider redissonClientListProvider) {
        super(config, redissonClientListProvider.initClients().toArray(new RedissonClient[0]));
        this.isSpin = Objects.requireNonNull(config.getSpin(), "isSpin argument is required");
        this.lockLeaseMills = config.getLockLeaseMills();
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
    public Lock createSimpleLock(String lockPath) {
        return new RedissonSimpleLock(this.lockLeaseMills, lockPath, getClient(lockPath));
    }

    @Override
    public ReadWriteLock createReadWriteLock(String lockPath) {
        return new RedissonReadWriteLock(lockPath, getClient(lockPath));
    }
}
