package com.vt.vtserver.service.Messaging;

import com.vt.vtserver.model.StationaryObject;
import com.vt.vtserver.repository.StationaryObjectRepository;
import com.vt.vtserver.service.StationaryObjectService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GeoUtils {

    private final StationaryObjectService stationaryObjectService;
    private final double MINIMUM_RANGE = 8;

    @AllArgsConstructor
    private class AlarmInfoUnit{
        public double rmin;
        public double tmin;
        public boolean collision_detected;
    }

    public void CheckOnCollision(MessageUnit messageUnit){
        //Todo check method
        List<StationaryObject> stationaryObjectList = stationaryObjectService.getAllStationaryObjects();
        for (StationaryObject object : stationaryObjectList) {
            if(CheckOnCollisionWithObject(messageUnit, object).collision_detected){
                //Todo alarm method
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
