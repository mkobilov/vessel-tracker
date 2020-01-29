package com.vt.vtserver.service.Messaging;

import com.vt.vtserver.model.Alarm;
import com.vt.vtserver.model.StationaryObject;
import com.vt.vtserver.model.Vessel;
import com.vt.vtserver.repository.AlarmRepository;
import com.vt.vtserver.repository.StationaryObjectRepository;
import com.vt.vtserver.repository.VesselRepository;
import com.vt.vtserver.service.AlarmService;
import com.vt.vtserver.service.StationaryObjectService;
import com.vt.vtserver.service.VesselService;
import com.vt.vtserver.web.rest.dto.AlarmDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeoUtils {
    private final StationaryObjectRepository stationaryObjectRepository;
    private final VesselRepository vesselRepository;
    private final AlarmRepository alarmRepository;
    private final double MINIMUM_RANGE = 8;



    @AllArgsConstructor
    private class AlarmInfoUnit{
        public double rmin;
        public double tmin;
        public boolean collision_detected;
    }

    public void CheckOnCollision(MessageUnit messageUnit){
        AlarmService alarmService = new AlarmService(alarmRepository);
        StationaryObjectService stationaryObjectService = new StationaryObjectService(stationaryObjectRepository);
        VesselService vesselService = new VesselService(vesselRepository);

        List<StationaryObject> stationaryObjectList = stationaryObjectService.getAllStationaryObjects();
        for (StationaryObject object : stationaryObjectList) {
            AlarmInfoUnit alarmInfoUnit = CheckOnCollisionWithObject(messageUnit, object);
            if(alarmInfoUnit.collision_detected){
                alarmService.postAlarm(new AlarmDTO(messageUnit.id, object.getId(),
                                         alarmInfoUnit.tmin, alarmInfoUnit.rmin));
                log.warn("Collision incoming" + object.getId());
                return;
            }
        }
        List<Vessel> vesselList = vesselService.getAll();
        for(Vessel vessel : vesselList){
            AlarmInfoUnit alarmInfoUnit = CheckOnCollisionWithVessel(messageUnit, vessel);
            if(alarmInfoUnit.collision_detected){
                alarmService.postAlarm(new AlarmDTO(messageUnit.id, vessel.getId(),
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
    private AlarmInfoUnit CheckOnCollisionWithVessel(MessageUnit messageUnit, Vessel vessel){
        double x01 = messageUnit.x;
        double y01 = messageUnit.y;
        double vx1 = messageUnit.vx;
        double vy1 = messageUnit.vy;
        //Todo x,y,vx,vy for vessel
        double x02 = 0;
        double y02 = 0;
        double vx2 = 0;
        double vy2 = 0;

        double tmin = ((y02*vy2+y01*vy1-y02*vy1-y01*vy2) + x02*vx2+x01*vy1-x01*vx2-x02*vy1)
                /((vy2*vy2+vy1*vy1-2*vy1*vy2)+(vx2*vx2+vx1*vx1-2*vx1*vx2));
        double rmin = Math.sqrt(Math.scalb((x02-x01+vx2*tmin-vx1*tmin),2) +
                Math.scalb((y02-y01+vy2*tmin-vy1*tmin),2));

        return new AlarmInfoUnit(rmin, tmin, rmin <= MINIMUM_RANGE);
    }
}
