package com.vt.vtserver.service.Asterix;



import lombok.extern.slf4j.Slf4j;
import org.opengis.referencing.FactoryException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class AsterixListener {

    private String mode;
    private boolean isLogEnabled;
    private String allowedCategories;

    private final RadarDataWriter radarDataWriter;

    public AsterixListener(RadarDataWriter radarDataWriter){
        this.radarDataWriter = radarDataWriter;
        this.mode = "tcp";
    }

    public void runAsterixListener(BlockingQueue<byte[]> rawRadarQueue, TcpManager tcpManager){
        try {
            System.out.println("Java Asterix Decoder Encoder");
//            BlockingQueue<byte[]> rawQueue = new ArrayBlockingQueue<>(4000);
            if(!mode.equals("udp") && !mode.equals("tcp")){
                throw new RuntimeException("Invalid mode. First parameter must be tcp, udp or file.");
            }
            switch (mode){
                case "tcp":{
                    ParseTcpData(rawRadarQueue,
                            this.radarDataWriter, tcpManager);
                    break;
                }
                default:{
                    throw new RuntimeException("Invalid mode. First parameter must be udp or file.");
                }
            }

        } catch (Exception ex){
            log.error("Asterix data listener initialization error", ex);
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

