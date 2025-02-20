package io.github.evanmi.distribute.lock.api;

public interface LockFactory {
    String lockPathSeparator();
    String lockPrefixPath();

    default String getDistributeLockPath(String lockKey) {
        return lockPrefixPath() + lockPathSeparator() + lockKey;
    }

    /**
     * Create normal lock
     * @param lockPath lock resource's path
     * @return normal lock
     */
    Lock createLock(String lockPath);

    /**
     * Create simple lock
     * @param lockPath lock resource's path
     * @return simple lock
     */
    Lock createSimpleLock(String lockPath);

    /**
     * Create readWrite lock
     * @param lockPath lock resource's path
     * @return readWrite lock
     */
    ReadWriteLock createReadWriteLock(String lockPath);
}
