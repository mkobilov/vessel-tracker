package com.vt.vtserver.service.Messaging;

import com.vt.vtserver.model.StationaryObject;
import com.vt.vtserver.model.Target;
import com.vt.vtserver.service.AlarmService;
import com.vt.vtserver.service.StationaryObjectService;
import com.vt.vtserver.service.TargetService;
import com.vt.vtserver.web.rest.dto.AlarmDTO;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeoUtils {
    @Autowired
    private MeterRegistry meterRegistry;
    @Autowired
    private Timer timer;
//    @Autowired
//    private Timer timerDB;

    //private final StationaryObjectRepository stationaryObjectRepository;
    private final TargetService targetService;
    private final StationaryObjectService stationaryObjectService;
    private final AlarmService alarmService;
    //Todo change value if required
    private final double minimumStationaryObjectRange = 30;
    private final double minimumTargetObjectRange = 10;
    private final double maximumCollisionTime = 1800;


    @AllArgsConstructor
    private static class AlarmInfoUnit {
        public double rmin;
        public double tmin;
        public boolean collision_detected;
    }

    public void CheckOnCollision(MessageUnit messageUnit) throws InterruptedException {
        Timer.Sample sample = Timer.start(meterRegistry);
        List<StationaryObject> stationaryObjectList = stationaryObjectService.getAllStationaryObjects();
        List<Target> targetList = targetService.getLatestTargets();
        List<AlarmDTO> alarms = new ArrayList<>();
        for (StationaryObject object : stationaryObjectList) {
            AlarmInfoUnit alarmInfoUnit = CheckOnCollisionWithObject(messageUnit, object);
            if (alarmInfoUnit.collision_detected) {
                Timestamp collisionTime = new Timestamp(System.currentTimeMillis() + (long) (1000 * alarmInfoUnit.tmin));

                AlarmDTO dto = new AlarmDTO(messageUnit.id, object.getId(),
                        collisionTime, alarmInfoUnit.rmin);
                alarms.add(dto);

            }
        }


        for (Target target : targetList) {
            AlarmInfoUnit alarmInfoUnit = CheckOnCollisionWithVessel(messageUnit, target);
            if (alarmInfoUnit.collision_detected) {
                Timestamp collisionTime = new Timestamp(System.currentTimeMillis() + (long) (1000 * alarmInfoUnit.tmin));

                AlarmDTO dto = new AlarmDTO(messageUnit.id, target.getTrackNumber(),
                        collisionTime, alarmInfoUnit.rmin);
                alarms.add(dto);
            }
        }
        sample.stop(timer);
        alarmService.postAlarms(alarms);
//        Thread.sleep(120000,0);
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
                Math.scalb((y0 - y1 + vy * tmin), 2));

        return new AlarmInfoUnit(rmin, tmin, (rmin <= minimumStationaryObjectRange) &&
                tmin > 0 &&
                tmin < 1800);
    }

    private AlarmInfoUnit CheckOnCollisionWithVessel(MessageUnit messageUnit, Target target) {

        if (target == null) {
            return new AlarmInfoUnit(0, 0, false);
        }


        double x10 = messageUnit.x;
        double y10 = messageUnit.y;
        double v1x = messageUnit.vx;
        double v1y = messageUnit.vy;
        double x20 = target.getX();
        double y20 = target.getY();
        double v2x = target.getVx();
        double v2y = target.getVy();


        double dx = (x10 - x20);
        double dy = (y10 - y20);
        double dvx = (v1x - v2x);
        double dvy = (v1y - v2y);


        double tmin = -(dx * dvx + dy * dvy) / (dvy * dvy + dvx * dvx);
        double rmin = Math.sqrt(Math.scalb((dx + dvx * tmin), 2) +
                Math.scalb((dy + dvx * tmin), 2));

        return new AlarmInfoUnit(rmin, tmin, (rmin <= minimumTargetObjectRange)
                && tmin > 0
                && tmin < 1800);
    }
}
