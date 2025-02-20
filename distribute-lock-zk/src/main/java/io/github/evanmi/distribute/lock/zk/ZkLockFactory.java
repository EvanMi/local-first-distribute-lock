package io.github.evanmi.distribute.lock.zk;

import io.github.evanmi.distribute.lock.api.AbstractClientLockFactory;
import io.github.evanmi.distribute.lock.api.Lock;
import io.github.evanmi.distribute.lock.api.ReadWriteLock;
import io.github.evanmi.distribute.lock.api.config.LockPrefixConfig;
import org.apache.curator.framework.CuratorFramework;

public class ZkLockFactory extends AbstractClientLockFactory<CuratorFramework, LockPrefixConfig> {

    public ZkLockFactory(LockPrefixConfig config, CuratorFrameworkListProvider curatorFrameworkListProvider) {
        super(config, curatorFrameworkListProvider.initClients().toArray(new CuratorFramework[0]));
    }

    @Override
    public String lockPathSeparator() {
        return "/";
    }

    @Override
    public Lock createLock(String lockPath) {
        return new ZkLock(lockPath, getClient(lockPath));
    }

    @Override
    public ReadWriteLock createReadWriteLock(String lockPath) {
        return new ZkReadWriteLock(lockPath, getClient(lockPath));
    }

    @Override
    public Lock createSimpleLock(String lockPath) {
        return new ZkSimpleLock(lockPath, getClient(lockPath));
    }
}
