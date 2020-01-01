package com.vt.vtserver.service.Asterix;

import com.vt.vtserver.web.rest.dto.TargetDTO;
import jlg.jade.asterix.AsterixDataBlock;
import jlg.jade.asterix.AsterixDecoder;
import jlg.jade.asterix.AsterixRecord;
import jlg.jade.asterix.cat062.Cat062Record;
import jlg.jade.asterix.counters.DefaultDecodingReport;
import org.opengis.referencing.FactoryException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Component
public class RadarDatagramConverter implements Runnable{
    private final BlockingQueue<byte[]> rawQueue;
    private boolean isLogEnabled = false;
    private int numberOfQueueItems;
    private int numberOfReceivedBytes;
    private int numberOfReceivedBytesFinalFrame;

    private String allowedCategories = "62";
    private final RadarDataWriter radarDataWriter;

    //coefficient to get Latitude and Longtitude from Asterix 62 frame
    private final double ASTERIX_LATITUDE_RATIO = 5.364418029785156e-6;

    RadarDatagramConverter(BlockingQueue<byte[]> rawQueue,
                           RadarDataWriter radarDataWriter) throws FactoryException {
        this.rawQueue = rawQueue;
        this.radarDataWriter = radarDataWriter;
    }

    @Override
    public void run() {
        //initialise item counters
        DefaultDecodingReport asterixDecodingReport = new DefaultDecodingReport();


        System.out.println("Start Datagram Convertor");

        //init asterix decoder
        List<Integer> categoriesToDecode = new ArrayList<>();
        if (allowedCategories.contains("62")) {
            categoriesToDecode.add(62);
        }
        if (allowedCategories.contains("65")) {
            categoriesToDecode.add(65);
        }
        if (allowedCategories.contains("4")) {
            categoriesToDecode.add(4);
        }
        if(allowedCategories.contains("150")){
            categoriesToDecode.add(150);
        }
        if(allowedCategories.contains("48")){
            categoriesToDecode.add(48);
        }
        if(allowedCategories.contains("34")){
            categoriesToDecode.add(34);
        }

        AsterixDecoder asterixDecoder = new AsterixDecoder(categoriesToDecode);

        long startTime = System.currentTimeMillis();
        int index = 0;
        while (true) {
            try {
                byte[] rawData = rawQueue.take();
                try {
                    List<AsterixDataBlock> dataBlocks = asterixDecoder.decode(
                            rawData,
                            0,
                            rawData.length
                    );
                    numberOfQueueItems++;
                    numberOfReceivedBytes += rawData.length;
                    numberOfReceivedBytesFinalFrame += rawData.length + 12;
                    if (isLogEnabled) {
                        for (AsterixDataBlock adb : dataBlocks) {
                            List<AsterixRecord> asterixRecordList = adb.getRecords();
                            for (AsterixRecord asterixRecord:asterixRecordList){
                                Cat062Record record = asterixRecord.getCat062Record();
                                OffsetDateTime now = OffsetDateTime.now( ZoneOffset.UTC );
                                OffsetDateTime dt = OffsetDateTime.of(LocalDateTime.of(now.getYear(),
                                        now.getMonth(), now.getDayOfMonth(), record.getItem070().getHours(),
                                        record.getItem070().getMinutes(), record.getItem070().getSeconds()),
                                        ZoneOffset.UTC);
                                //System.out.println(record.getDebugString());
                            }

                            asterixDecodingReport.update(adb);
                        }
                        index++;

//                        System.out.println("Processed " +
//                                numberOfQueueItems + " datagrams/packets (" +
//                                numberOfReceivedBytes +
//                                ") bytes (" + numberOfReceivedBytesFinalFrame + ") received bytes in " +
//                                "FF. Elapsed time " +
//                                (System.currentTimeMillis() - startTime) / 1000 + " sec");
                    }

                    // Write radar targets to database
                    for (AsterixDataBlock adb : dataBlocks) {
                        List<AsterixRecord> asterixRecordList = adb.getRecords();
                        for (AsterixRecord asterixRecord:asterixRecordList){
                            Cat062Record record = asterixRecord.getCat062Record();
                            OffsetDateTime now = OffsetDateTime.now( ZoneOffset.UTC );
                            OffsetDateTime dt = OffsetDateTime.of(LocalDateTime.of(now.getYear(),
                                    now.getMonth(), now.getDayOfMonth(), record.getItem070().getHours(),
                                    record.getItem070().getMinutes(), record.getItem070().getSeconds()),
                                    ZoneOffset.UTC);

                            double latImpl = ((double)record.getItem105().getLatitudeWsg84()) * ASTERIX_LATITUDE_RATIO;
                            double lonImpl = ((double)record.getItem105().getLongitudeWsg84()) * ASTERIX_LATITUDE_RATIO;

                            TargetDTO dto = new TargetDTO(
                                    record.getItem010().getSac(),
                                    record.getItem010().getSic(),
                                    dt,
                                    latImpl,
                                    lonImpl,
                                    record.getItem105().getLatitudeWsg84(),
                                    record.getItem105().getLatitudeWsg84(),
                                    record.getItem185().getVx(),
                                    record.getItem185().getVy(),
                                    Long.valueOf(record.getItem040().getTrackNb()),
                                    record.getItem080().getCfnValue(),
                                    record.getItem080().getFpcValue(),
                                    record.getItem080().getMonValue(),
                                    record.getItem080().getMrhValue(),
                                    record.getItem080().getSimValue(),
                                    record.getItem080().getSpiValue(),
                                    record.getItem080().getSrcValue(),
                                    record.getItem080().getTseValue()
                            );

                            this.radarDataWriter.writeRadar(dto, now);


                        }
                        asterixDecodingReport.update(adb);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
