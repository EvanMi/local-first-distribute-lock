package io.github.evanmi.distribute.lock.db;

import io.github.evanmi.distribute.lock.api.config.LockPrefixConfig;

public class DbLockConfig extends LockPrefixConfig {
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
}
