package io.github.evanmi.db.springboot.starter.example;

import io.github.evanmi.db.springboot.starter.example.service.LockTestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DbExampleApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DbExampleApplication.class, args);

        LockTestService lockTestService = context.getBean(LockTestService.class);

        try {
            lockTestService.testLock();
            lockTestService.testReadWriteLock();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
