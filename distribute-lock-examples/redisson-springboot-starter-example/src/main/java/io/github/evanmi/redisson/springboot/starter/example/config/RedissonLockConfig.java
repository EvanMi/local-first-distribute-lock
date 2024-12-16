package io.github.evanmi.redisson.springboot.starter.example.config;

import io.github.evanmi.distribute.lock.redisson.RedissonClientListProvider;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RedissonLockConfig {

    // simple redisson config with redisson-springboot-starter
    // you can create as many redissonClients as you want
    @Bean
    public RedissonClientListProvider redissonClientListProvider(RedissonClient redissonClient) {
        RedissonClientListProvider redissonClientListProvider = new RedissonClientListProvider() {
            @Override
            public List<RedissonClient> initClients() {
                List<RedissonClient> redissonClients = new ArrayList<>();
                redissonClients.add(redissonClient);
                return redissonClients;
            }
        };
        return redissonClientListProvider;
    }
}
