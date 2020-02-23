package com.vt.vtserver.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableAutoConfiguration
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    // Asterix listener
    private String radarIP;
    private Integer radarPort;

    //Rabbit config
    private long frequency;
    private String queue;
    private int consumersNumber;
    private int vesselMinimalRange;
    private int stationaryObjectMinimalRange;
    private int maximumCollisionTimeSeconds;
    private int categoryToDecode;
    private int numberOfThreadsForRadarReceive;

}
