package io.github.evanmi.zk.springboot.starter.example.config;

import io.github.evanmi.distribute.lock.zk.CuratorFrameworkListProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.common.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ZkLockConfig {

    @Bean
    public CuratorFrameworkListProvider curatorFrameworkListProvider() {
        CuratorFrameworkListProvider curatorFrameworkListProvider = new CuratorFrameworkListProvider() {
            @Override
            public List<CuratorFramework> initClients() {
                String zkAddress ="testpubli.zk.com:2181;testpubli.zk.com:2181";
                String username = "admin;admin";
                String password = "admin888;admin888";

                if (StringUtils.isBlank(zkAddress)) {
                    throw new IllegalStateException("zkAddress can't be null");
                }

                String[] zkAddressArr = zkAddress.split(";");
                String[] usernameArr = null;
                String[] passwordArr = null;

                if (!StringUtils.isBlank(username) && !StringUtils.isBlank(password)) {
                    usernameArr = username.split(";");
                    passwordArr = password.split(";");
                }
                if (null != usernameArr && (usernameArr.length != passwordArr.length || usernameArr.length != zkAddressArr.length)) {
                    throw new IllegalStateException("username,password and zkAddress arr must have the same length");
                }
                List<CuratorFramework> curatorFrameworks =  new ArrayList<>(zkAddressArr.length);
                for (int i = 0; i < zkAddressArr.length; i++) {
                    CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                            .connectString(zkAddressArr[i])
                            .retryPolicy(new ExponentialBackoffRetry(200, 3));
                    if (null != usernameArr) {
                        builder.authorization("digest",
                                (usernameArr[i] + ":" + passwordArr[i]).getBytes());
                    }
                    CuratorFramework buildClient = builder.build();
                    buildClient.start();
                    curatorFrameworks.add(buildClient);
                }
                return curatorFrameworks;
            }
        };
        return curatorFrameworkListProvider;
	}
}
