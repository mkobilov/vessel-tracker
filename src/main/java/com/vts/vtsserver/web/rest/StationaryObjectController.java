package com.vts.vtsserver.web.rest;

import com.vts.vtsserver.model.StationaryObject;
import com.vts.vtsserver.service.StationaryObjectService;
import com.vts.vtsserver.web.rest.dto.StationaryObjectDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stobj")
public class StationaryObjectController {
    private final StationaryObjectService stationaryObjectService;

    @PostMapping
    public ResponseEntity<StationaryObject> postObj(@RequestBody StationaryObjectDTO dto){
        return stationaryObjectService.postStObj(dto);
    }
    @GetMapping
    public ResponseEntity<List<StationaryObject>> getObj(){
        return stationaryObjectService.getStObj();
    }
}
