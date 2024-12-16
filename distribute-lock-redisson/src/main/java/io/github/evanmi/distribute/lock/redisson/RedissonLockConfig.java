package io.github.evanmi.distribute.lock.redisson;

import io.github.evanmi.distribute.lock.api.config.LockPrefixConfig;

public class RedissonLockConfig extends LockPrefixConfig {
    private Boolean isSpin;

    public Boolean getSpin() {
        return isSpin;
    }

    public void setSpin(Boolean spin) {
        isSpin = spin;
    }
}
