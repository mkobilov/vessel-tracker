package com.vt.vtserver.service;

import com.vt.vtserver.model.StationaryObject;
import com.vt.vtserver.model.Target;
import com.vt.vtserver.repository.TargetRepository;
import com.vt.vtserver.web.rest.dto.TargetDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TargetService {
    private final TargetRepository targetRepository;


    public List<Target> getAllTargets() {
        return targetRepository.findAll();
    }


    //This method is a crutch for grafana worldmap plugin, sadly it can only
    //work with one query at a time
    public void postStationaryObject(StationaryObject stationaryObject, Long trackNumber) {

        Target target = new Target();
        target.setCreationTime(OffsetDateTime.now(ZoneOffset.UTC));
        target.setDateTime(OffsetDateTime.now(ZoneOffset.UTC));
        target.setLat(stationaryObject.getLat());
        target.setLon(stationaryObject.getLon());
        target.setTrackNumber(trackNumber);

        target.setVx((double) 0);
        target.setVy((double) 0);

        target.setStationaryObject((short) 1);
        targetRepository.save(target);
    }

    public Target postTarget(TargetDto dto) {
        try {
            Target target = new Target(dto);
            target.setCreationTime(OffsetDateTime.now(ZoneOffset.UTC));
            target.setDateTime(OffsetDateTime.now(ZoneOffset.UTC));
            target = targetRepository.save(target);

            return target;
        } catch (Exception e) {
            log.error("Error posting new vessel" + dto, e);
            return null;
        }

    }

    public List<Target> getLatestTargets() {
        return targetRepository.getLatestTargetPositions(1);
    }


}
