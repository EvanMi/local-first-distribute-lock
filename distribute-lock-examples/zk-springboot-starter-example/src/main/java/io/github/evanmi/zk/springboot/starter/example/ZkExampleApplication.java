package io.github.evanmi.zk.springboot.starter.example;

import io.github.evanmi.zk.springboot.starter.example.service.LockTestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ZkExampleApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ZkExampleApplication.class, args);

        LockTestService lockTestService = context.getBean(LockTestService.class);

        try {
            //lockTestService.testLock();
            //lockTestService.testReadWriteLock();
            for (int i = 0 ; i < 20; i++) {
                lockTestService.testSimpleLock();
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
