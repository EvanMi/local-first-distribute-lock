package io.github.evanmi.distribute.lock.core;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.ReentrantLock;

public class SimpleLockTest {
    private static LocalLockCacheConfig localLockCacheConfig;
    private static final String testLockKey = "testLock";

    @BeforeAll
    static void initLocalLockCacheConfig() {
        localLockCacheConfig = new LocalLockCacheConfig();
        localLockCacheConfig.setMaximumSize(2);
        localLockCacheConfig.setDuration(20);
    }

    @Test
    void testSimpleLockCreate() {
        SimpleLock simpleLock = new SimpleLock(localLockCacheConfig, LockFactoryCreateHelper.createMockLockFactory());
        Assertions.assertNotNull(simpleLock);
    }

    @Test
    void testSimpleLockNewLocalLock() {
        SimpleLock simpleLock = new SimpleLock(localLockCacheConfig, LockFactoryCreateHelper.createMockLockFactory());
        ReentrantLock reentrantLock = simpleLock.newLocalLock(testLockKey);
        Assertions.assertNotNull(reentrantLock);
    }

    @Test
    void testSimpleLockTryLockExecuteSuccess() {
        SimpleLock simpleLock = new SimpleLock(localLockCacheConfig, LockFactoryCreateHelper.createMockLockFactory());
        try {
            simpleLock.tryLockExecute(testLockKey, () -> {
                System.out.println("LockTryLockExecute 执行成功");
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSimpleLockTryLockSubmitSuccess() {
        SimpleLock simpleLock = new SimpleLock(localLockCacheConfig, LockFactoryCreateHelper.createMockLockFactory());
        try {
            Assertions.assertTrue(simpleLock.tryLockSubmit(testLockKey, () -> true).orElseThrow(RuntimeException::new));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
