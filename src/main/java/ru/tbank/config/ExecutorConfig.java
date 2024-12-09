package ru.tbank.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
@ConfigurationProperties(prefix = "thread")
public class ExecutorConfig {
    private int poolSize;

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getPoolSize() {
        return poolSize;
    }

    @Bean
    @Qualifier("fixedThreadPool")
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(poolSize, r -> {
            Thread thread = new Thread(r);
            thread.setName("FixedThreadPool-" + thread.getId());
            return thread;
        });
    }

    @Bean
    @Qualifier("scheduledThreadPool")
    public ExecutorService scheduledThreadPool() {
        return Executors.newScheduledThreadPool(1);
    }
}

