package com.vt.vtserver.service.Asterix;


import lombok.extern.slf4j.Slf4j;
import org.opengis.referencing.FactoryException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class AsterixListener {
    private final RadarDataWriter radarDataWriter;

    public AsterixListener(RadarDataWriter radarDataWriter) {
        this.radarDataWriter = radarDataWriter;
    }

    public void runAsterixListener(BlockingQueue<byte[]> rawRadarQueue, TcpManager tcpManager) {

        log.info("Asterix listener decoder/encoder");

        try {
            ParseTcpData(rawRadarQueue,
                    this.radarDataWriter, tcpManager);
        } catch (FactoryException e) {
            log.error("Error parsing tcp data, radar datagram converter creation failed", e);
        }
    }

    private static void ParseTcpData(BlockingQueue<byte[]> rawRadarQueue,
                                     RadarDataWriter radarDataWriter, TcpManager tcpManager) throws FactoryException {
        TcpReader tcpRadarReader = new TcpReader(rawRadarQueue, tcpManager);
        RadarDatagramConverter radarDatagramConverter =
                new RadarDatagramConverter(rawRadarQueue, radarDataWriter);
        ThreadPoolExecutor radarExecutorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        radarExecutorService.submit(tcpRadarReader);
        radarExecutorService.submit(radarDatagramConverter);
    }
}

