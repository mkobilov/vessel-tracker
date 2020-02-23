package com.vt.vtserver.service.Messaging;

import com.vt.vtserver.config.ApplicationProperties;
import com.vt.vtserver.model.StationaryObject;
import com.vt.vtserver.model.Target;
import com.vt.vtserver.service.AlarmService;
import com.vt.vtserver.service.StationaryObjectService;
import com.vt.vtserver.service.TargetService;
import com.vt.vtserver.web.rest.dto.AlarmDto;
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
    @Autowired
    private ApplicationProperties applicationProperties;

//    @Autowired
//    private Timer timerDB;

    //private final StationaryObjectRepository stationaryObjectRepository;
    private final TargetService targetService;
    private final StationaryObjectService stationaryObjectService;
    private final AlarmService alarmService;


    @AllArgsConstructor
    private static class AlarmInfoUnit {
        public double minimalRange;
        public double estimatedCollisionTimeSeconds;
        public boolean collisionDetected;
    }

    public void CheckOnCollision(MessageUnit messageUnit) throws InterruptedException {
        Timer.Sample sample = Timer.start(meterRegistry);
        List<StationaryObject> stationaryObjectList = stationaryObjectService.getAllStationaryObjects();
        List<Target> targetList = targetService.getLatestTargets();

        List<AlarmDto> alarms = new ArrayList<>();

        for (StationaryObject object : stationaryObjectList) {
            AlarmInfoUnit alarmInfoUnit = CheckOnCollisionWithObject(messageUnit, object);

            if (alarmInfoUnit.collisionDetected) {
                Timestamp collisionTime = new Timestamp(System.currentTimeMillis()
                        + (long) (1000 * alarmInfoUnit.estimatedCollisionTimeSeconds));

                AlarmDto dto = new AlarmDto(messageUnit.getId(), object.getId(), collisionTime, alarmInfoUnit.minimalRange);
                alarms.add(dto);
            }
        }

        for (Target target : targetList) {
            AlarmInfoUnit alarmInfoUnit = CheckOnCollisionWithVessel(messageUnit, target);
            if (alarmInfoUnit.collisionDetected) {
                Timestamp collisionTime = new Timestamp(System.currentTimeMillis()
                        + (long) (1000 * alarmInfoUnit.estimatedCollisionTimeSeconds));

                AlarmDto dto = new AlarmDto(messageUnit.getId(), target.getTrackNumber(),
                        collisionTime, alarmInfoUnit.minimalRange);
                alarms.add(dto);
            }
        }
        sample.stop(timer);
        alarmService.postAlarms(alarms);
//        Thread.sleep(120000,0);
    }

    private AlarmInfoUnit CheckOnCollisionWithObject(MessageUnit messageUnit, StationaryObject stationaryObject) {

        double startX = messageUnit.getX();
        double startY = messageUnit.getY();
        double vx = messageUnit.getVx();
        double vy = messageUnit.getVy();

        double stationaryObjectX = stationaryObject.getX();
        double stationaryObjectY = stationaryObject.getY();
        //time (in seconds, from the moment of calculation)
        // when distance between vessel and stationary object hits its minimum value
        double estimatedCollisionTimeSeconds = -(vx * (startX - stationaryObjectX) + vy * (startY - stationaryObjectY))
                / (vx * vx + vy * vy);
        double minimalRange = Math.sqrt(Math.scalb((startX - stationaryObjectX + vx * estimatedCollisionTimeSeconds), 2)
                + Math.scalb((startY - stationaryObjectY + vy * estimatedCollisionTimeSeconds), 2));

        boolean collisionDetected = minimalRange <= applicationProperties.getStationaryObjectMinimalRange()
                && estimatedCollisionTimeSeconds > 0
                && estimatedCollisionTimeSeconds < applicationProperties.getMaximumCollisionTimeSeconds();

        return new AlarmInfoUnit(minimalRange, estimatedCollisionTimeSeconds, collisionDetected);
    }

    private AlarmInfoUnit CheckOnCollisionWithVessel(MessageUnit messageUnit, Target target) {

        if (target == null) {
            return new AlarmInfoUnit(0, 0, false);
        }

        double firstShipStartX = messageUnit.getX();
        double firstShipStartY = messageUnit.getY();
        double firstShipVx = messageUnit.getVx();
        double firstShipVy = messageUnit.getVy();

        double secondShipStartX = target.getX();
        double secondShipStartY = target.getY();
        double secondShipVx = target.getVx();
        double secondShipVy = target.getVy();

        double coordinateDifferenceX = (firstShipStartX - secondShipStartX);
        double coordinateDifferenceY = (firstShipStartY - secondShipStartY);
        double speedDifferenceX = (firstShipVx - secondShipVx);
        double speedDifferenceY = (firstShipVy - secondShipVy);


        double estimatedCollisionTimeSeconds = -(coordinateDifferenceX * speedDifferenceX
                + coordinateDifferenceY * speedDifferenceY)
                / (speedDifferenceY * speedDifferenceY + speedDifferenceX * speedDifferenceX);
        double minimalRange = Math.sqrt(Math.scalb((coordinateDifferenceX +
                speedDifferenceX * estimatedCollisionTimeSeconds), 2)
                + Math.scalb((coordinateDifferenceY + speedDifferenceX * estimatedCollisionTimeSeconds), 2));

        return new AlarmInfoUnit(minimalRange, estimatedCollisionTimeSeconds,
                (minimalRange <= applicationProperties.getVesselMinimalRange())
                        && estimatedCollisionTimeSeconds > 0
                        && estimatedCollisionTimeSeconds < applicationProperties.getMaximumCollisionTimeSeconds());
    }

//    private static class ShipForComparison {
//        Pair<Integer, Integer> startingPosition;
//        Pair<Integer, Integer> startingSpeed;
//    }
//
//    private AlarmInfoUnit CheckOnCollisionWithVessel(Pair<ShipForComparison, ShipForComparison> pair) {
//        ShipForComparison first = pair.getKey();
//        ShipForComparison second = pair.getValue();
//        return new AlarmInfoUnit();
//    }
}
