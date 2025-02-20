package io.github.evanmi.distribute.lock.redisson;

import io.github.evanmi.distribute.lock.api.Lock;
import io.github.evanmi.distribute.lock.api.renew.AutoRenewLock;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class RedissonSimpleLock extends AutoRenewLock implements Lock {
    private final String path;
    private final RedissonClient redissonClient;
    private final String lockUUID = UUID.randomUUID().toString();
    private final Long lockLeaseMills;

    public RedissonSimpleLock(Long lockLeaseMills, String path, RedissonClient redissonClient) {
        super(lockLeaseMills);
        if (null == lockLeaseMills || lockLeaseMills < 1000) {
            lockLeaseMills = 1000L;
        }
        this.lockLeaseMills = lockLeaseMills;
        this.path = path;
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean acquire(long nanos) throws Exception {
        boolean locked = this.redissonClient.getBucket(path, StringCodec.INSTANCE)
                .setIfAbsent(getLockData(), Duration.ofMillis(this.lockLeaseMills));

        if (locked) {
            scheduleExpirationRenewal(getThreadId(), this.path);
            return true;
        }
        return false;
    }

    @Override
    public void release() throws Exception {
        String releaseScript = "local key = KEYS[1];" +
                "local lockVal = redis.call('GET', key);" +
                "if ARGV[1] == lockVal then " +
                "    redis.call('DEL', key);" +
                "end;";
        this.redissonClient.getScript(StringCodec.INSTANCE).eval(RScript.Mode.READ_WRITE, releaseScript,
                RScript.ReturnType.VALUE, Collections.singletonList(path), getLockData());
        cancelExpirationRenewal(getThreadId(), this.path);
    }

    private String getLockData() {
        return this.lockUUID + "#" + getThreadId();
    }

    private String getLockData(String threadId) {
        return this.lockUUID + "#" + threadId;
    }

    @Override
    protected CompletionStage<Boolean> renewExpirationAsync(String threadId) {
        String releaseScript = "local key = KEYS[1];" +
                "local expMills = tonumber(ARGV[1]);" +
                "local lockVal = redis.call('GET', key);" +
                "if ARGV[2] == lockVal then " +
                "    redis.call('PEXPIRE', key, expMills);" +
                "    return 1;" +
                "end;" +
                "return 0;";
        return this.redissonClient.getScript(StringCodec.INSTANCE).evalAsync(RScript.Mode.READ_WRITE, releaseScript,
                RScript.ReturnType.BOOLEAN, Collections.singletonList(this.path),
                String.valueOf(this.lockLeaseMills), getLockData(threadId));
    }
}
