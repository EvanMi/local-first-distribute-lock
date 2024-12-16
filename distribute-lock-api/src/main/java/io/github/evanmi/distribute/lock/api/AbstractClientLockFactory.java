package io.github.evanmi.distribute.lock.api;

import io.github.evanmi.distribute.lock.api.config.LockPrefixConfig;


import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.evanmi.distribute.lock.api.crc16.CRC16Table.calculateCRC16;

public abstract class AbstractClientLockFactory <C, P extends LockPrefixConfig> implements LockFactory {
    protected final P config;
    protected final C[] clients;

    public AbstractClientLockFactory(P config, C[] clients) {
        this.config = config;
        this.clients = clients;
    }

    public C getClient(String key) {
        return this.clients[calculateCRC16(key.getBytes()) % this.clients.length];
    }

    @Override
    public String lockPrefixPath() {
        return this.config.getLockPrefix();
    }
}
