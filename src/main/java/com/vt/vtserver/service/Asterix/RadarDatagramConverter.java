package com.vt.vtserver.service.Asterix;

import com.vt.vtserver.web.rest.dto.TargetDto;
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
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Component
@Slf4j
public class RadarDatagramConverter implements Runnable {


    private final BlockingQueue<byte[]> rawQueue;
    //Category of ASTERIX
    private final RadarDataWriter radarDataWriter;

    //coefficient to get Latitude and Longitude from Asterix 62 frame
    private static final double ASTERIX_LATITUDE_RATIO = 5.364418029785156e-6;

    RadarDatagramConverter(BlockingQueue<byte[]> rawQueue,
                           RadarDataWriter radarDataWriter) throws FactoryException {
        this.rawQueue = rawQueue;
        this.radarDataWriter = radarDataWriter;
    }

    @Override
    public void run() {
        //initialise item counters
        DefaultDecodingReport asterixDecodingReport = new DefaultDecodingReport();
        //init asterix decoder
        AsterixDecoder asterixDecoder = new AsterixDecoder(CommonConstants.ALLOWED_CATEGORY);

        //observer pattern is not applicable here, because data coming from queue is too frequent, so we would use
        // too much computer resources.
        while (true) {
            try {
                byte[] rawData = rawQueue.take();
                List<AsterixDataBlock> dataBlocks = asterixDecoder.decode(
                        rawData,
                        0,
                        rawData.length
                );

                // Write radar targets to database
                for (AsterixDataBlock adb : dataBlocks) {
                    List<AsterixRecord> asterixRecordList = adb.getRecords();

                    for (AsterixRecord asterixRecord : asterixRecordList) {
                        Cat062Record record = asterixRecord.getCat062Record();
                        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

                        OffsetDateTime dt = OffsetDateTime.of(LocalDateTime.of(now.getYear(),
                                now.getMonth(), now.getDayOfMonth(), record.getItem070().getHours(),
                                record.getItem070().getMinutes(), record.getItem070().getSeconds()),
                                ZoneOffset.UTC);

                        double latImpl = ((double) record.getItem105().getLatitudeWsg84())
                                * CommonConstants.ASTERIX_LATITUDE_RATIO;
                        double lonImpl = ((double) record.getItem105().getLongitudeWsg84())
                                * CommonConstants.ASTERIX_LATITUDE_RATIO;

                        TargetDto dto = new TargetDto(
                                record.getItem010().getSac(),
                                record.getItem010().getSic(),
                                dt,
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
            } catch (InterruptedException e) {
                log.error("ASTERIX parse error, raw queue", e);
            } catch (Exception e) {
                log.error("ASTERIX parse error", e);
                rawQueue.clear();
            }
        }
    }
}
