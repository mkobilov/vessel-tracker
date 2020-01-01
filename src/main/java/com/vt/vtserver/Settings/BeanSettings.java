package com.vt.vtserver.Settings;

import com.vt.vtserver.config.ApplicationProperties;
import com.vt.vtserver.repository.TargetRepository;
import com.vt.vtserver.service.Asterix.AsterixListener;
import com.vt.vtserver.service.Asterix.RadarDataWriter;
import com.vt.vtserver.service.Asterix.TcpManager;
import lombok.RequiredArgsConstructor;
import org.opengis.referencing.FactoryException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;

@Service
@RequiredArgsConstructor
public class BeanSettings {
    private final ApplicationProperties applicationProperties;
    private final TargetRepository radarTargetRepository;
    private  final RabbitTemplate rabbitTemplate;
    private final BlockingQueue<byte[]> rawQueueRadar;
    @Bean
    RadarDataWriter radarDataWriter() throws FactoryException { return new RadarDataWriter(radarTargetRepository, rabbitTemplate, applicationProperties); }


    @PostConstruct
    private void init() throws FactoryException {

        TcpManager tcpManager = new TcpManager(rawQueueRadar);
        tcpManager.setIp(applicationProperties.getRadarIP());
        tcpManager.setPort(applicationProperties.getRadarPort());
        // Run radar listener
        AsterixListener asterixRadarListener = new AsterixListener(radarDataWriter());
        asterixRadarListener.runAsterixListener(rawQueueRadar, tcpManager);


    }
}
