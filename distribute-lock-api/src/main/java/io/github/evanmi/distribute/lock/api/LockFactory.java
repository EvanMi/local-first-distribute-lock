package io.github.evanmi.distribute.lock.api;

public interface LockFactory {
    String lockPathSeparator();
    String lockPrefixPath();

    default String getDistributeLockPath(String lockKey) {
        return lockPrefixPath() + lockPathSeparator() + lockKey;
    }
    Lock createLock(String lockPath);
    ReadWriteLock createReadWriteLock(String lockPath);
}
