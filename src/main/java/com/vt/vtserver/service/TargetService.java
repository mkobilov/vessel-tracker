package com.vt.vtserver.service;

import com.vt.vtserver.model.Target;
import com.vt.vtserver.repository.TargetRepository;
import com.vt.vtserver.web.rest.dto.TargetDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TargetService {
    private final TargetRepository targetRepository;

    private void changeTarget(Target target, TargetDTO dto) {
        target.setCfn(dto.getCfn());
        target.setDateTime(dto.getDateTime());
        target.setFpc(dto.getFpc());
        target.setLat(dto.getLat());
        target.setLon(dto.getLon());
        target.setMon(dto.getMon());
        target.setMrh(dto.getMrh());
        target.setSac(dto.getSac());
        target.setSim(dto.getSim());
        target.setSin(dto.getSin());
        target.setSpi(dto.getSpi());
        target.setSrc(dto.getSrc());
        target.setTrackNumber(dto.getTrackNumber());
        target.setTse(dto.getTse());

        target.setVx((double) dto.getVx());
        target.setVy((double) dto.getVy());



        target.setDateTime(dto.getDateTime());
    }

    public List<Target> getAllTargets() {
        try {
            return targetRepository.findAll();
        } catch (Exception e) {
            log.error("Service Error getVessel :", e);
            return null;
        }
    }

    public Target postTarget(TargetDTO dto, OffsetDateTime creationTime) {
        try {
            log.debug("postVessel start");
            Target target = new Target();
            changeTarget(target, dto);
            target.setCreationTime(creationTime);
            target = targetRepository.save(target);

            return target;
        }catch (Exception e){
            log.error("Error posting new vessel" + dto, e);
            return null;
        }

    }


}
