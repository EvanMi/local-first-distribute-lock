package io.github.evanmi.db.springboot.starter.example.config;

import io.github.evanmi.distribute.lock.db.DataSourceListProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

@Configuration
public class DbLockConfig {
    @Bean
    public DataSourceListProvider dataSourceListProvider(DataSource dataSource) {
        return new DataSourceListProvider() {
            @Override
            public List<DataSource> initClients() {
                return Collections.singletonList(dataSource);
            }
        };
	}
}
