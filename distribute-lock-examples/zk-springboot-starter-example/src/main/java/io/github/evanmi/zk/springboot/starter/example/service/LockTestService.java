package io.github.evanmi.zk.springboot.starter.example.service;


import io.github.evanmi.distribute.lock.core.NormalLock;
import io.github.evanmi.distribute.lock.core.ReadWriteLock;
import io.github.evanmi.distribute.lock.core.SimpleLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Service
public class LockTestService {
    private static final Logger logger = LoggerFactory.getLogger(LockTestService.class);
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);


    @Resource
    private ReadWriteLock localFirstZkReadWriteLock;

    @Resource
    private NormalLock localFirstZkLock;

    @Resource
    private SimpleLock localFirstZkSimpleLock;

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1L, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            if (Thread.currentThread().isInterrupted()) {
                Thread.interrupted();
            }
            executorService.shutdownNow();
        }
    }

    public void testSimpleLock() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5);
        for (int i = 0; i < 5; i++) {
            executorService.execute(() -> {
                try {
                    cyclicBarrier.await();
                    Optional<Long> currentMills = localFirstZkSimpleLock.tryLockSubmit("simple-lock/key",  () -> {
                        logger.info("lock-doing");
                        return System.currentTimeMillis();
                    });
                    if (!currentMills.isPresent()) {
                        logger.info("currentMills: {}", currentMills);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void testReadWriteLock() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(10);

        for (int i = 0; i < 5; i++) {
            executorService.execute(() -> {
                try {
                    cyclicBarrier.await();
                    localFirstZkReadWriteLock.tryReadLockExecute("readWriteLock/key", 3, TimeUnit.SECONDS, () -> {
                        logger.info("read-lock-doing");
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        for (int i = 0; i < 5; i++) {
            executorService.execute(() -> {
                try {
                    cyclicBarrier.await();
                    Optional<Long> currentMills = localFirstZkReadWriteLock.tryWriteLockSubmit("readWriteLock/key", 3, TimeUnit.SECONDS, () -> {
                        logger.info("write-lock-doing");
                        return System.currentTimeMillis();
                    });
                    logger.info("currentMills: {}", currentMills);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void testLock() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5);
        for (int i = 0; i < 5; i++) {
            executorService.execute(() -> {
                try {
                    cyclicBarrier.await();
                    Optional<Long> currentMills = localFirstZkLock.tryLockSubmit("lock/key", 3, TimeUnit.SECONDS, () -> {
                        logger.info("lock-doing");
                        return System.currentTimeMillis();
                    });
                    logger.info("currentMills: {}", currentMills);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

}