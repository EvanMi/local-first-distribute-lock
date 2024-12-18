package io.github.evanmi.distribute.lock.db.springboot.starter.boot;

import io.github.evanmi.distribute.lock.api.util.StringUtils;
import io.github.evanmi.distribute.lock.core.LocalLockCacheConfig;
import io.github.evanmi.distribute.lock.core.NormalLock;
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
        config.setLockLeaseMills(Objects.requireNonNullElse(distributeLockDbConfig.getLockLeaseMills(), 30000L));
        config.setTableName(StringUtils.isBlank(distributeLockDbConfig.getTableName()) ? "t_distribute_lock"
                : distributeLockDbConfig.getTableName());
        return new DbLockFactory(config, redissonClientListProvider);
    }

    @Bean
    @ConditionalOnMissingBean(name = "localFirstDbLock")
    public NormalLock localFirstDbLock(DistributeLockDbConfig distributeLockDbConfig,
                                       DbLockFactory dbLockFactory) {
        LocalLockCacheConfig localLockCacheConfig = new LocalLockCacheConfig();
        localLockCacheConfig.setDuration(Objects.requireNonNullElse(distributeLockDbConfig.getDuration(), 120L));
        localLockCacheConfig.setMaximumSize(Objects.requireNonNullElse(distributeLockDbConfig.getMaxSize(), 1000L));
        return new NormalLock(localLockCacheConfig, dbLockFactory);
    }
}
