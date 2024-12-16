package io.github.evanmi.distribute.lock.api;

public interface Lock {

    boolean acquire(long nanos) throws Exception;

    void release() throws Exception;
}
