package io.github.evanmi.distribute.lock.core;

import io.github.evanmi.distribute.lock.api.Lock;
import io.github.evanmi.distribute.lock.api.LockFactory;
import io.github.evanmi.distribute.lock.api.ReadWriteLock;

public class LockFactoryCreateHelper {

    static LockFactory createMockLockFactory() {

        return new LockFactory() {
            @Override
            public String lockPathSeparator() {
                return "#";
            }

            @Override
            public String lockPrefixPath() {
                return "mock";
            }

            @Override
            public Lock createLock(String lockPath) {
                return new Lock() {
                    @Override
                    public boolean acquire(long nanos) {
                        return true;
                    }

                    @Override
                    public void release() {

                    }
                };
            }

            @Override
            public Lock createSimpleLock(String lockPath) {
                return new Lock() {
                    @Override
                    public boolean acquire(long nanos) {
                        return true;
                    }

                    @Override
                    public void release() {

                    }
                };
            }

            @Override
            public ReadWriteLock createReadWriteLock(String lockPath) {
                return new ReadWriteLock() {
                    @Override
                    public Lock readLock() {
                        return new Lock() {
                            @Override
                            public boolean acquire(long nanos) {
                                return true;
                            }

                            @Override
                            public void release() {

                            }
                        };
                    }

                    @Override
                    public Lock writeLock() {
                        return new Lock() {
                            @Override
                            public boolean acquire(long nanos) {
                                return true;
                            }

                            @Override
                            public void release() {

                            }
                        };
                    }
                };
            }
        };
    }
}
