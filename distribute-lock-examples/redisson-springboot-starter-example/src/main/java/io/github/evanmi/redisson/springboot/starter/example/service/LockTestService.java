package io.github.evanmi.redisson.springboot.starter.example.service;


import io.github.evanmi.distribute.lock.core.NormalLock;
import io.github.evanmi.distribute.lock.core.ReadWriteLock;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    private ReadWriteLock localFirstRedissonReadWriteLock;

    @Resource
    private NormalLock localFirstRedissonLock;

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


    public void testReadWriteLock() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(10);

        for (int i = 0; i < 5; i++) {
            executorService.execute(() -> {
                try {
                    cyclicBarrier.await();
                    localFirstRedissonReadWriteLock.tryReadLockExecute("readWriteLock:key", 3, TimeUnit.SECONDS, () -> {
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
                    Optional<Long> currentMills = localFirstRedissonReadWriteLock.tryWriteLockSubmit("readWriteLock/key", 3, TimeUnit.SECONDS, () -> {
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
                    Optional<Long> currentMills = localFirstRedissonLock.tryLockSubmit("lock/key", 3, TimeUnit.SECONDS, () -> {
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