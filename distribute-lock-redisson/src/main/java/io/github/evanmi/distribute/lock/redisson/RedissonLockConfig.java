package io.github.evanmi.distribute.lock.redisson;

import io.github.evanmi.distribute.lock.api.config.LockPrefixConfig;

public class RedissonLockConfig extends LockPrefixConfig {
    private Boolean isSpin;
    private Long lockLeaseMills;

    public Boolean getSpin() {
        return isSpin;
    }

    public void setSpin(Boolean spin) {
        isSpin = spin;
    }

    public Long getLockLeaseMills() {
        return lockLeaseMills;
    }

    public void setLockLeaseMills(Long lockLeaseMills) {
        this.lockLeaseMills = lockLeaseMills;
    }
}
