package io.github.evanmi.distribute.lock.api;


public interface ReadWriteLock {
    Lock readLock();

    Lock writeLock();
}
