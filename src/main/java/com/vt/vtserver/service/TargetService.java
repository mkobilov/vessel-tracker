package com.vt.vtserver.service;

import com.vt.vtserver.model.Target;
import com.vt.vtserver.repository.TargetRepository;
import com.vt.vtserver.web.rest.dto.TargetDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TargetService {
    private final TargetRepository targetRepository;

    private void changeTarget(Target target, TargetDTO dto) {
        target.setHeading(dto.getHeading());
        target.setSpeed(dto.getSpeed());
        target.setLat(dto.getLat());
        target.setLon(dto.getLon());
        target.setVx(dto.getVx());
        target.setVy(dto.getVy());
        target.setX(dto.getX());
        target.setY(dto.getY());

        target.setDateTime(dto.getDateTime());
    }

    public List<Target> getAllTargets() {
        try {
            List<Target> list = targetRepository.findAll();

            return list;
        } catch (Exception e) {
            log.error("Service Error getVessel :", e);
            return null;
        }
    }

    public Target postTarget(TargetDTO dto) {
        try {
            log.debug("postVessel start");
            Target target = new Target();
            changeTarget(target, dto);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            target.setCreationTime(timestamp);
            target = targetRepository.save(target);

            return target;
        }catch (Exception e){
            log.error("Error posting new vessel" + dto, e);
            return null;
        }

    }


}
