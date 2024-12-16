package io.github.evanmi.distribute.lock.db;

import io.github.evanmi.distribute.lock.api.AbstractClientLockFactory;
import io.github.evanmi.distribute.lock.api.Lock;
import io.github.evanmi.distribute.lock.api.ReadWriteLock;

import javax.sql.DataSource;

public class DbLockFactory extends AbstractClientLockFactory<DataSource, DbLockConfig> {
    public DbLockFactory(DbLockConfig config, DataSourceListProvider dataSourceListProvider) {
        super(config, dataSourceListProvider.initClients().toArray(new DataSource[0]));
    }

    @Override
    public String lockPathSeparator() {
        return "_";
    }

    @Override
    public Lock createLock(String lockPath) {
        return new DbLock(config.getLockLeaseMills(), lockPath,
                config.getTableName(), getClient(lockPath));
    }

    @Override
    public ReadWriteLock createReadWriteLock(String lockPath) {
        throw new IllegalStateException("readWriteLock not supported");
    }
}
