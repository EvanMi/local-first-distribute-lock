package io.github.evanmi.distribute.lock.zk;

import org.apache.curator.framework.CuratorFramework;

import java.util.List;

public interface CuratorFrameworkListProvider {
    List<CuratorFramework> initClients();
}
