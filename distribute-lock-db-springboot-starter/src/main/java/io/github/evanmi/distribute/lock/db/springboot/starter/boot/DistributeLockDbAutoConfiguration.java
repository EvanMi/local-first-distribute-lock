package io.github.evanmi.distribute.lock.db.springboot.starter.boot;

import io.github.evanmi.distribute.lock.api.util.StringUtils;
import io.github.evanmi.distribute.lock.core.LocalLockCacheConfig;
import io.github.evanmi.distribute.lock.core.NormalLock;
import io.github.evanmi.distribute.lock.core.SimpleLock;
import io.github.evanmi.distribute.lock.db.DataSourceListProvider;
import io.github.evanmi.distribute.lock.db.DbLockConfig;
import io.github.evanmi.distribute.lock.db.DbLockFactory;
import io.github.evanmi.distribute.lock.db.springboot.starter.config.DistributeLockDbConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;


@Configuration
@ConditionalOnBean(DataSourceListProvider.class)
@EnableConfigurationProperties(DistributeLockDbConfig.class)
public class DistributeLockDbAutoConfiguration {

    @Bean
    public DbLockFactory redissonLockFactory(DistributeLockDbConfig distributeLockDbConfig,
                                             DataSourceListProvider redissonClientListProvider) {
        DbLockConfig config = new DbLockConfig();
        config.setLockPrefix(StringUtils.isBlank(distributeLockDbConfig.getLockPrefix()) ? "yumi-lock"
                : distributeLockDbConfig.getLockPrefix());
        config.setLockLeaseMills(distributeLockDbConfig.getLockLeaseMills() == null ? 30000L : distributeLockDbConfig.getLockLeaseMills());
        config.setTableName(StringUtils.isBlank(distributeLockDbConfig.getTableName()) ? "t_distribute_lock"
                : distributeLockDbConfig.getTableName());
        return new DbLockFactory(config, redissonClientListProvider);
    }

    @Bean
    @ConditionalOnMissingBean(name = "localFirstDbLock", value = NormalLock.class)
    public NormalLock localFirstDbLock(DistributeLockDbConfig distributeLockDbConfig,
                                       DbLockFactory dbLockFactory) {
        LocalLockCacheConfig localLockCacheConfig = new LocalLockCacheConfig();
        localLockCacheConfig.setDuration(distributeLockDbConfig.getDuration() == null ? 120L : distributeLockDbConfig.getDuration());
        localLockCacheConfig.setMaximumSize(distributeLockDbConfig.getMaxSize() == null ? 1000L : distributeLockDbConfig.getMaxSize());
        return new NormalLock(localLockCacheConfig, dbLockFactory);
    }

    @Bean
    @ConditionalOnMissingBean(name = "localFirstDbSimpleLock", value = SimpleLock.class)
    public SimpleLock localFirstRedissonSimpleLock(DistributeLockDbConfig distributeLockDbConfig,
                                                   DbLockFactory dbLockFactory) {
        LocalLockCacheConfig localLockCacheConfig = new LocalLockCacheConfig();
        localLockCacheConfig.setDuration(distributeLockDbConfig.getDuration() == null ? 120L : distributeLockDbConfig.getDuration());
        localLockCacheConfig.setMaximumSize(distributeLockDbConfig.getMaxSize() == null ? 1000L : distributeLockDbConfig.getMaxSize());
        return new SimpleLock(localLockCacheConfig, dbLockFactory);
    }
}
