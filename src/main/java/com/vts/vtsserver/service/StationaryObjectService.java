package com.vts.vtsserver.service;

import com.vts.vtsserver.model.StationaryObject;
import com.vts.vtsserver.repository.StationaryObjectRepository;
import com.vts.vtsserver.web.rest.dto.StationaryObjectDTO;
import lombok.Data;
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
@Data
public class StationaryObjectService {
    private final StationaryObjectRepository stationaryObjectRepository;

    private void changeStObj(StationaryObject stObj, StationaryObjectDTO dto){
        stObj.setName(dto.getName());
        stObj.setDescription(dto.getDescription());
        stObj.setLat(dto.getLat());
        stObj.setLon(dto.getLon());
    }

    public ResponseEntity<StationaryObject> postStObj(StationaryObjectDTO dto){
        try{
            StationaryObject stObj = new StationaryObject();
            changeStObj(stObj, dto);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            stObj.setCreationTime(timestamp);
            stationaryObjectRepository.save(stObj);

            return ResponseEntity.ok(stObj);
        } catch (Exception e) {
            log.error("Service Err posting stationary object:");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<List<StationaryObject>> getStObj() {
        try{
            List<StationaryObject> list = stationaryObjectRepository.findAll();
            return new ResponseEntity<List<StationaryObject>>(list, HttpStatus.OK);
        } catch (Exception e) {
            log.error("service Error getting all stationary objects");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }
}
