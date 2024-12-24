package io.github.evanmi.distribute.lock.redisson.springboot.starter.boot;

import io.github.evanmi.distribute.lock.api.util.StringUtils;
import io.github.evanmi.distribute.lock.core.LocalLockCacheConfig;
import io.github.evanmi.distribute.lock.core.NormalLock;
import io.github.evanmi.distribute.lock.core.ReadWriteLock;
import io.github.evanmi.distribute.lock.redisson.RedissonClientListProvider;
import io.github.evanmi.distribute.lock.redisson.RedissonLockConfig;
import io.github.evanmi.distribute.lock.redisson.RedissonLockFactory;
import io.github.evanmi.distribute.lock.redisson.springboot.starter.config.DistributeLockRedissonConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.UUID;


@Configuration
@ConditionalOnBean(RedissonClientListProvider.class)
@EnableConfigurationProperties(DistributeLockRedissonConfig.class)
public class DistributeLockRedissonAutoConfiguration {

    @Bean
    public RedissonLockFactory redissonLockFactory(DistributeLockRedissonConfig distributeLockRedissonConfig,
                                                   RedissonClientListProvider redissonClientListProvider) {
        RedissonLockConfig config = new RedissonLockConfig();
        config.setLockPrefix(StringUtils.isBlank(distributeLockRedissonConfig.getLockPrefix()) ? "yumi-lock"
                : distributeLockRedissonConfig.getLockPrefix());
        config.setSpin(Objects.requireNonNullElse(distributeLockRedissonConfig.getSpin(), Boolean.FALSE));
        RedissonLockFactory redissonLockFactory = new RedissonLockFactory(config, redissonClientListProvider);
        return redissonLockFactory;
    }

    @Bean
    @ConditionalOnMissingBean(name = "localFirstRedissonLock", value = NormalLock.class)
    public NormalLock localFirstRedissonLock(DistributeLockRedissonConfig distributeLockRedissonConfig,
                                             RedissonLockFactory redissonLockFactory) {
        LocalLockCacheConfig localLockCacheConfig = new LocalLockCacheConfig();
        localLockCacheConfig.setDuration(Objects.requireNonNullElse(distributeLockRedissonConfig.getDuration(), 120L));
        localLockCacheConfig.setMaximumSize(Objects.requireNonNullElse(distributeLockRedissonConfig.getMaxSize(), 1000L));
        return new NormalLock(localLockCacheConfig, redissonLockFactory);
    }

    @Bean
    @ConditionalOnMissingBean(name = "localFirstRedissonReadWriteLock", value = ReadWriteLock.class)
    public ReadWriteLock localFirstRedissonReadWriteLock(DistributeLockRedissonConfig distributeLockRedissonConfig,
                                                   RedissonLockFactory redissonLockFactory) {
        LocalLockCacheConfig localLockCacheConfig = new LocalLockCacheConfig();
        localLockCacheConfig.setDuration(Objects.requireNonNullElse(distributeLockRedissonConfig.getDuration(), 120L));
        localLockCacheConfig.setMaximumSize(Objects.requireNonNullElse(distributeLockRedissonConfig.getMaxSize(), 1000L));
        return new ReadWriteLock(localLockCacheConfig, redissonLockFactory);
    }
}
