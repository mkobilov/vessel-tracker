package com.vt.vtserver.service.Messaging;

import com.vt.vtserver.model.Alarm;
import com.vt.vtserver.model.StationaryObject;
import com.vt.vtserver.repository.AlarmRepository;
import com.vt.vtserver.repository.StationaryObjectRepository;
import com.vt.vtserver.service.AlarmService;
import com.vt.vtserver.service.StationaryObjectService;
import com.vt.vtserver.web.rest.dto.AlarmDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeoUtils {
    private final StationaryObjectRepository stationaryObjectRepository;
    private final AlarmRepository alarmRepository;
    private final double MINIMUM_RANGE = 8;



    @AllArgsConstructor
    private class AlarmInfoUnit{
        public double rmin;
        public double tmin;
        public boolean collision_detected;
    }

    public void CheckOnCollision(MessageUnit messageUnit){
        assert alarmRepository != null;
        assert stationaryObjectRepository != null;
        AlarmService alarmService = new AlarmService(alarmRepository);
        StationaryObjectService stationaryObjectService = new StationaryObjectService(stationaryObjectRepository);

        List<StationaryObject> stationaryObjectList = stationaryObjectService.getAllStationaryObjects();
        for (StationaryObject object : stationaryObjectList) {
            AlarmInfoUnit alarmInfoUnit = CheckOnCollisionWithObject(messageUnit, object);
            if(alarmInfoUnit.collision_detected){
                alarmService.postAlarm(new AlarmDTO(messageUnit.id, object.getId(),
                                         alarmInfoUnit.tmin, alarmInfoUnit.rmin));
                return;
            }
        }

    }

    private AlarmInfoUnit CheckOnCollisionWithObject(MessageUnit messageUnit, StationaryObject stationaryObject){
        double x0 = messageUnit.x;
        double y0 = messageUnit.y;
        double vx = messageUnit.vx;
        double vy = messageUnit.vy;

        double x1 = stationaryObject.getX();
        double y1 = stationaryObject.getY();
        //time (in seconds, from the moment of calculation)
        // when distance between vessel and stationary object hits its minimum value
        double tmin = -(vx*(x0-x1)+vy*(y0-y1))/(vx*vx + vy*vy);
        double rmin = Math.sqrt(Math.scalb((x0-x1+vx*tmin),2) +
                      Math.scalb((y0-y1+vx*tmin),2));

        return new AlarmInfoUnit(rmin, tmin, rmin <= MINIMUM_RANGE);
    }
}
