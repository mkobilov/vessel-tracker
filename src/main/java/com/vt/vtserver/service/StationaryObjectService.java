package com.vt.vtserver.service;

import com.vt.vtserver.model.StationaryObject;
import com.vt.vtserver.repository.StationaryObjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StationaryObjectService {
    private final StationaryObjectRepository stationaryObjectRepository;

    public List<StationaryObject> getAllStationaryObjects() {
        try {
            return stationaryObjectRepository.findAll();
        } catch (Exception e) {
            log.error("Service Error getVessel :", e);
            return null;
        }
    }
}
