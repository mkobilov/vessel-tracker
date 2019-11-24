package com.vts.vtsserver.service;

import com.vts.vtsserver.model.Vessel;
import com.vts.vtsserver.repository.VesselRepository;
import com.vts.vtsserver.web.rest.dto.VesselDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VesselService {
    private final VesselRepository vesselRepository;

    private void changeVessel(Vessel vessel, VesselDTO dto){
        vessel.setName(dto.getName());
        vessel.setDescription(dto.getDescription());
        vessel.setHeading(dto.getHeading());
        vessel.setSpeed(dto.getSpeed());
        vessel.setLat(dto.getLat());
        vessel.setLon(dto.getLon());
    }

    public ResponseEntity<List<Vessel>> getVesselBySpeed(Double speed){
        try {
            List<Vessel> list = vesselRepository.findBySpeedGreaterThan(speed);

            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Service Error getVessel with speed:" + speed, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    public ResponseEntity<List<Vessel>> getVessel(){
        try {
            List<Vessel> list = vesselRepository.findAll();

            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Service Error getVessel :", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    public ResponseEntity<Vessel> getVessel(Long id){
        try {
            Vessel vessel = vesselRepository.findById(id).orElse(null);

            return ResponseEntity.ok(vessel);
        } catch (Exception e) {
            log.error("Service Error getVessel with id" + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<Vessel> postVessel(VesselDTO dto){
        try {
            log.debug("postVessel start");
            Vessel vessel = new Vessel();
            changeVessel(vessel, dto);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            vessel.setCreationTime(timestamp);
            vessel = vesselRepository.save(vessel);

            return ResponseEntity.ok(vessel);
        }catch (Exception e){
            log.error("Error posting new vessel" + dto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<Vessel> updateVessel(VesselDTO dto, Long id) {
        try{
            Vessel vessel = vesselRepository.findById(id).orElse(null);
            changeVessel(vessel, dto);
            vessel = vesselRepository.save(vessel);

            return ResponseEntity.ok(vessel);
        } catch (Exception e) {
            log.warn("Service Error while updating vessel with id:" + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    public ResponseEntity<Vessel> updateVessel(VesselDTO dto, String name) {
        try{
            Vessel vessel = vesselRepository.findByName(name).stream().findAny().orElseThrow(() ->new Exception());
            changeVessel(vessel, dto);
            vessel = vesselRepository.save(vessel);

            return ResponseEntity.ok(vessel);
        } catch (Exception e) {
            log.warn("Service Error while updating vessel with name:" + dto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
