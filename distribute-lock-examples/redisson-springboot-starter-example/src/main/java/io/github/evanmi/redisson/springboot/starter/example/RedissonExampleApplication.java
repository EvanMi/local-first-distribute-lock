package io.github.evanmi.redisson.springboot.starter.example;

import io.github.evanmi.redisson.springboot.starter.example.service.LockTestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RedissonExampleApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RedissonExampleApplication.class, args);

        LockTestService lockTestService = context.getBean(LockTestService.class);

        try {
            lockTestService.testLock();
            lockTestService.testReadWriteLock();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
