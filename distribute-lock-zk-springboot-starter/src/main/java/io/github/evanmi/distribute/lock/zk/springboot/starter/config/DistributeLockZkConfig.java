package io.github.evanmi.distribute.lock.zk.springboot.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "local.first.zk.lock")
public class DistributeLockZkConfig {
    private String lockPrefix;
    private Long duration;
    private Long maxSize;


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
