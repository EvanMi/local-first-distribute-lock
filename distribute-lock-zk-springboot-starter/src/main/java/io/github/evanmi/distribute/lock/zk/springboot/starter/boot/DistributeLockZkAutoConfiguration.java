package io.github.evanmi.distribute.lock.zk.springboot.starter.boot;

import io.github.evanmi.distribute.lock.api.config.LockPrefixConfig;
import io.github.evanmi.distribute.lock.api.util.StringUtils;
import io.github.evanmi.distribute.lock.core.LocalLockCacheConfig;
import io.github.evanmi.distribute.lock.core.NormalLock;
import io.github.evanmi.distribute.lock.core.ReadWriteLock;
import io.github.evanmi.distribute.lock.zk.CuratorFrameworkListProvider;
import io.github.evanmi.distribute.lock.zk.ZkLockFactory;
import io.github.evanmi.distribute.lock.zk.springboot.starter.config.DistributeLockZkConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.UUID;


@Configuration
@ConditionalOnBean(CuratorFrameworkListProvider.class)
@EnableConfigurationProperties(DistributeLockZkConfig.class)
public class DistributeLockZkAutoConfiguration {

    @Bean
    public ZkLockFactory zkLockFactory(DistributeLockZkConfig distributeLockZkConfig,
                                       CuratorFrameworkListProvider curatorFrameworkListProvider) {
        LockPrefixConfig lockPrefixConfig = new LockPrefixConfig();
        lockPrefixConfig.setLockPrefix(StringUtils.isBlank(distributeLockZkConfig.getLockPrefix()) ? "/" + "yumi-lock"
                : distributeLockZkConfig.getLockPrefix());
        ZkLockFactory zkLockFactory = new ZkLockFactory(lockPrefixConfig, curatorFrameworkListProvider);
        return zkLockFactory;
    }

    @Bean
    @ConditionalOnMissingBean(name = "localFirstZkLock")
    public NormalLock localFirstZkLock(DistributeLockZkConfig distributeLockZkConfig,
                                       ZkLockFactory zkLockFactory) {
        LocalLockCacheConfig localLockCacheConfig = new LocalLockCacheConfig();
        localLockCacheConfig.setDuration(Objects.requireNonNullElse(distributeLockZkConfig.getDuration(), 120L));
        localLockCacheConfig.setMaximumSize(Objects.requireNonNullElse(distributeLockZkConfig.getMaxSize(), 1000L));
        return new NormalLock(localLockCacheConfig, zkLockFactory);
    }

    @Bean
    @ConditionalOnMissingBean(name = "localFirstZkReadWriteLock")
    public ReadWriteLock localFirstZkReadWriteLock(DistributeLockZkConfig distributeLockZkConfig,
                                                   ZkLockFactory zkLockFactory) {
        LocalLockCacheConfig localLockCacheConfig = new LocalLockCacheConfig();
        localLockCacheConfig.setDuration(Objects.requireNonNullElse(distributeLockZkConfig.getDuration(), 120L));
        localLockCacheConfig.setMaximumSize(Objects.requireNonNullElse(distributeLockZkConfig.getMaxSize(), 1000L));
        return new ReadWriteLock(localLockCacheConfig, zkLockFactory);
    }
}
