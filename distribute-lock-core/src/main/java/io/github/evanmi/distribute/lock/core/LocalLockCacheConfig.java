package io.github.evanmi.distribute.lock.core;


public class LocalLockCacheConfig {
    /**
     * Local cache duration in SECOND
     */
    private Long duration;
    /**
     * Max Local cache size
     */
    private Long maximumSize;

    public Long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Long getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(long maximumSize) {
        this.maximumSize = maximumSize;
    }
}
