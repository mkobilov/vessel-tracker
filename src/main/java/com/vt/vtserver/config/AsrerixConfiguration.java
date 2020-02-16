package com.vt.vtserver.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Data
@Configuration
@EnableAutoConfiguration
public class AsrerixConfiguration {

    @Bean
    BlockingQueue<byte[]> rawQueue() {
        return new ArrayBlockingQueue<>(4000);
    }
}
