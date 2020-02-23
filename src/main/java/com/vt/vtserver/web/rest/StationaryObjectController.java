package com.vt.vtserver.web.rest;

import com.vt.vtserver.model.StationaryObject;
import com.vt.vtserver.service.StationaryObjectService;
import com.vt.vtserver.web.rest.dto.StationaryObjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stationaryObject")
public class StationaryObjectController {

    public final StationaryObjectService stationaryObjectService;

    @GetMapping
    public ResponseEntity<List<StationaryObject>> getAllStationaryObjects() {
        List<StationaryObject> list = stationaryObjectService.getAllStationaryObjects();
        if (list != null)
            return ResponseEntity.ok(list);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @PostMapping
    public ResponseEntity<StationaryObject> postStationaryObject(@RequestBody StationaryObjectDto dto) {
        StationaryObject stationaryObject = null;
        try {
            stationaryObject = stationaryObjectService.postStationaryObject(dto);
        } catch (TransformException | FactoryException e) {
            log.warn("Transform error, object coordinates are wrong", e);
        }

        if (stationaryObject != null) {
            return ResponseEntity.ok(stationaryObject);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
