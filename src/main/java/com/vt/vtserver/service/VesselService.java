package com.vt.vtserver.service;

import com.vt.vtserver.model.Vessel;
import com.vt.vtserver.repository.VesselRepository;
import com.vt.vtserver.web.rest.dto.VesselDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VesselService {
    private final VesselRepository vesselRepository;


    public List<Vessel> getVesselBySpeed(Double speed) {
        return vesselRepository.findBySpeedGreaterThan(speed);
    }

    public List<Vessel> getAll() {
        return vesselRepository.findAll();
    }

    public Vessel getVessel(Long id) {
        return vesselRepository.findById(id).orElse(null);
    }

    public Vessel postVessel(VesselDto dto) {
        Vessel vessel = new Vessel(dto);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        vessel.setCreationTime(timestamp);
        vessel = vesselRepository.save(vessel);

        return vessel;
    }

    public Vessel updateVessel(VesselDto dto, Long id) {

        Vessel vessel = vesselRepository.findById(id).orElse(null);

        if (vessel == null)
            return null;

        vessel.setName(dto.getName());
        vessel.setDescription(dto.getDescription());
        vessel.setHeading(dto.getHeading());
        vessel.setSpeed(dto.getSpeed());
        vessel.setLat(dto.getLat());
        vessel.setLon(dto.getLon());

        return vessel;
    }

    public Vessel updateVessel(VesselDto dto, String name) {

        Vessel vessel = vesselRepository.findByName(name).stream().findAny().orElseThrow(RuntimeException::new);
        vessel.setName(dto.getName());
        vessel.setDescription(dto.getDescription());
        vessel.setHeading(dto.getHeading());
        vessel.setSpeed(dto.getSpeed());
        vessel.setLat(dto.getLat());
        vessel.setLon(dto.getLon());

        vessel = vesselRepository.save(vessel);

        return vessel;
    }
}
