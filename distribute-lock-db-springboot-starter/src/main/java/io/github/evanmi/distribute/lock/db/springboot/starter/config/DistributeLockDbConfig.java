package io.github.evanmi.distribute.lock.db.springboot.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "local.first.db.lock")
public class DistributeLockDbConfig {
    private String lockPrefix;
    private Long duration;
    private Long maxSize;
    private String tableName;
    private Long lockLeaseMills;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getLockLeaseMills() {
        return lockLeaseMills;
    }

    public void setLockLeaseMills(Long lockLeaseMills) {
        this.lockLeaseMills = lockLeaseMills;
    }

    public String getLockPrefix() {
        return lockPrefix;
    }

    public void setLockPrefix(String lockPrefix) {
        this.lockPrefix = lockPrefix;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Long maxSize) {
        this.maxSize = maxSize;
    }
}
