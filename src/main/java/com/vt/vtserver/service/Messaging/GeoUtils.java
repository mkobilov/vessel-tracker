package com.vt.vtserver.service.Messaging;

import com.vt.vtserver.model.StationaryObject;
import com.vt.vtserver.service.AlarmService;
import com.vt.vtserver.service.StationaryObjectService;
import com.vt.vtserver.web.rest.dto.AlarmDTO;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeoUtils {
    @Autowired
    private MeterRegistry registry;
    @Autowired
    private Timer timer;

    //private final StationaryObjectRepository stationaryObjectRepository;
    private final StationaryObjectService stationaryObjectService;
    private final AlarmService alarmService;
    //Todo change
    private final double MINIMUM_RANGE = 30;


    @AllArgsConstructor
    private static class AlarmInfoUnit {
        public double rmin;
        public double tmin;
        public boolean collision_detected;
    }


    public void CheckOnCollision(MessageUnit messageUnit) {
        //Timer.Sample sample = Timer.start(registry);

        List<StationaryObject> stationaryObjectList = stationaryObjectService.getAllStationaryObjects();
        for (StationaryObject object : stationaryObjectList) {
            AlarmInfoUnit alarmInfoUnit = CheckOnCollisionWithObject(messageUnit, object);
            if (alarmInfoUnit.collision_detected) {
                Timestamp collisionTime = new Timestamp(System.currentTimeMillis() + (long) (1000 * alarmInfoUnit.tmin));

                AlarmDTO dto = new AlarmDTO(messageUnit.id, object.getId(),
                        collisionTime, alarmInfoUnit.rmin);
                //alarmService.deletePreviousAlarm(dto);
                alarmService.postAlarm(dto);
                return;
            }
        }

        //sample.stop(timer);
    }

    private AlarmInfoUnit CheckOnCollisionWithObject(MessageUnit messageUnit, StationaryObject stationaryObject) {
        double x0 = messageUnit.x;
        double y0 = messageUnit.y;
        double vx = messageUnit.vx;
        double vy = messageUnit.vy;

        double x1 = stationaryObject.getX();
        double y1 = stationaryObject.getY();
        //time (in seconds, from the moment of calculation)
        // when distance between vessel and stationary object hits its minimum value
        double tmin = -(vx * (x0 - x1) + vy * (y0 - y1)) / (vx * vx + vy * vy);
        double rmin = Math.sqrt(Math.scalb((x0 - x1 + vx * tmin), 2) +
                Math.scalb((y0 - y1 + vx * tmin), 2));

        return new AlarmInfoUnit(rmin, tmin, (rmin <= MINIMUM_RANGE) && tmin > 0);
    }
}
