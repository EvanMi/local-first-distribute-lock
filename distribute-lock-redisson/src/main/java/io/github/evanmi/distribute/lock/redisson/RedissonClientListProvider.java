package io.github.evanmi.distribute.lock.redisson;

import org.redisson.api.RedissonClient;

import java.util.List;

public interface RedissonClientListProvider {
    List<RedissonClient> initClients();
}
