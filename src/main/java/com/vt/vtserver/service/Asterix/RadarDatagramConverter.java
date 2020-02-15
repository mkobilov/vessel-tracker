package com.vt.vtserver.service.Asterix;

import com.vt.vtserver.web.rest.dto.TargetDTO;
import jlg.jade.asterix.AsterixDataBlock;
import jlg.jade.asterix.AsterixDecoder;
import jlg.jade.asterix.AsterixRecord;
import jlg.jade.asterix.cat062.Cat062Record;
import jlg.jade.asterix.counters.DefaultDecodingReport;
import lombok.extern.slf4j.Slf4j;
import org.opengis.referencing.FactoryException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Component
@Slf4j
public class RadarDatagramConverter implements Runnable{

    private final BlockingQueue<byte[]> rawQueue;
    private boolean isLogEnabled = false;
    private int numberOfQueueItems;
    private int numberOfReceivedBytes;
    private int numberOfReceivedBytesFinalFrame;
    //Category of ASTERIX
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


        AsterixDecoder asterixDecoder = new AsterixDecoder(categoriesToDecode);

        long startTime = System.currentTimeMillis();
        int index = 0;
        //todo clean
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
                            }

                            asterixDecodingReport.update(adb);
                        }
                        index++;

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
                                    //TODO CHECK WITH REAL DATA, MB ERR HERE
                                    lonImpl,
                                    latImpl,
                                    record.getItem105().getLatitudeWsg84(),
                                    record.getItem105().getLongitudeWsg84(),
                                    record.getItem185().getVx(),
                                    record.getItem185().getVy(),
                                    (long) record.getItem040().getTrackNb(),
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
                    log.error("ASTERIX parse error" + e);
                    rawQueue.clear();
                }

            } catch (InterruptedException e) {
                log.error("ASTERIX parse error, raw queue" + e);
            }
        }
    }


    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }


}
