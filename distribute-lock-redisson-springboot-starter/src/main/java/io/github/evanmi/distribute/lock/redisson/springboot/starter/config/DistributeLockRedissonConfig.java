package io.github.evanmi.distribute.lock.redisson.springboot.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "local.first.redisson.lock")
public class DistributeLockRedissonConfig {
    private String lockPrefix;
    private Long duration;
    private Long maxSize;
    private Boolean isSpin;
    private Long lockLeaseMills;

    public Boolean getSpin() {
        return isSpin;
    }

    public void setSpin(Boolean spin) {
        isSpin = spin;
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

    public Long getLockLeaseMills() {
        return lockLeaseMills;
    }

    public void setLockLeaseMills(Long lockLeaseMills) {
        this.lockLeaseMills = lockLeaseMills;
    }
}
