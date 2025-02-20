package io.github.evanmi.distribute.lock.zk;

import io.github.evanmi.distribute.lock.api.Lock;
import org.apache.curator.RetryLoop;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


public class ZkSimpleLock implements Lock {
    private final String lockPath;
    private final CuratorFramework client;
    private final String lockUUID = UUID.randomUUID().toString();
    private final Logger logger = LoggerFactory.getLogger(ZkSimpleLock.class);

    public ZkSimpleLock(String lockPath, CuratorFramework client) {
        this.lockPath = lockPath;
        this.client = client;
    }

    @Override
    public boolean acquire(long nanos) throws Exception {
        String localLockPath = this.lockPath;
        String localLockData = getLockData();
        CuratorFramework localClient = this.client;

        try {
            localClient.create()
                    .creatingParentContainersIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(localLockPath, localLockData.getBytes());
        } catch (KeeperException.NodeExistsException nodeExistsException) {
            return false;
        }

        long startMillis = System.currentTimeMillis();
        for (int retryCount = 0; ; ) {
            try {
                byte[] bytes = localClient.getData().forPath(localLockPath);
                if (null != bytes) {
                    return new String(bytes).equalsIgnoreCase(localLockData);
                }
                return false;
            } catch (KeeperException.NoNodeException noNodeException) {
                if (!localClient.getZookeeperClient()
                        .getRetryPolicy()
                        .allowRetry(
                                retryCount++,
                                System.currentTimeMillis() - startMillis,
                                RetryLoop.getDefaultRetrySleeper())) {
                    if (logger.isErrorEnabled()) {
                        logger.error("Lock create success, but get lock data failed! Lock path is: " + localLockPath);
                    }
                    throw noNodeException;
                }
            }
        }
    }

    @Override
    public void release() throws Exception {
        String localLockPath = this.lockPath;
        CuratorFramework localClient = this.client;

        byte[] bytes = localClient.getData().forPath(localLockPath);
        if (null != bytes) {
            String dataStr = new String(bytes);
            if (dataStr.equalsIgnoreCase(getLockData())) {
                localClient.delete().guaranteed().forPath(localLockPath);
            }
        }
    }

    private String getLockData() {
        return this.lockUUID + "#" + Thread.currentThread().getId();
    }
}
