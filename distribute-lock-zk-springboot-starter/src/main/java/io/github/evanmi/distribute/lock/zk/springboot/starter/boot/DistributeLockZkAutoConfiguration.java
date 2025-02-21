package io.github.evanmi.distribute.lock.zk.springboot.starter.boot;

import io.github.evanmi.distribute.lock.api.config.LockPrefixConfig;
import io.github.evanmi.distribute.lock.api.util.StringUtils;
import io.github.evanmi.distribute.lock.core.LocalLockCacheConfig;
import io.github.evanmi.distribute.lock.core.NormalLock;
import io.github.evanmi.distribute.lock.core.ReadWriteLock;
import io.github.evanmi.distribute.lock.core.SimpleLock;
import io.github.evanmi.distribute.lock.zk.CuratorFrameworkListProvider;
import io.github.evanmi.distribute.lock.zk.ZkLockFactory;
import io.github.evanmi.distribute.lock.zk.ZkSimpleLock;
import io.github.evanmi.distribute.lock.zk.springboot.starter.config.DistributeLockZkConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;


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
    @ConditionalOnMissingBean(name = "localFirstZkLock", value = NormalLock.class)
    public NormalLock localFirstZkLock(DistributeLockZkConfig distributeLockZkConfig,
                                       ZkLockFactory zkLockFactory) {
        return new NormalLock(getLocalLockCacheConfig(distributeLockZkConfig), zkLockFactory);
    }

    @Bean
    @ConditionalOnMissingBean(name = "localFirstZkReadWriteLock", value = ReadWriteLock.class)
    public ReadWriteLock localFirstZkReadWriteLock(DistributeLockZkConfig distributeLockZkConfig,
                                                   ZkLockFactory zkLockFactory) {
        return new ReadWriteLock(getLocalLockCacheConfig(distributeLockZkConfig), zkLockFactory);
    }

    @Bean
    @ConditionalOnMissingBean(name = "localFirstZkSimpleLock", value = ZkSimpleLock.class)
    public SimpleLock localFirstZkSimpleLock(DistributeLockZkConfig distributeLockZkConfig,
                                             ZkLockFactory zkLockFactory) {
        return new SimpleLock(getLocalLockCacheConfig(distributeLockZkConfig), zkLockFactory);
    }

    private static LocalLockCacheConfig getLocalLockCacheConfig(DistributeLockZkConfig distributeLockZkConfig) {
        LocalLockCacheConfig localLockCacheConfig = new LocalLockCacheConfig();
        localLockCacheConfig.setDuration(distributeLockZkConfig.getDuration() == null ? 120L : distributeLockZkConfig.getDuration());
        localLockCacheConfig.setMaximumSize(distributeLockZkConfig.getMaxSize() == null ? 1000L : distributeLockZkConfig.getMaxSize());
        return localLockCacheConfig;
    }
}
