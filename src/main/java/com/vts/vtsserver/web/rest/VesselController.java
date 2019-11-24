package com.vts.vtsserver.web.rest;

import com.vts.vtsserver.model.Vessel;
import com.vts.vtsserver.service.VesselService;
import com.vts.vtsserver.web.rest.dto.VesselDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vessel")
public class VesselController {
    private final VesselService vesselService;
    @PostMapping
    public ResponseEntity<Vessel> postVessel(@RequestBody VesselDTO dto){

        return vesselService.postVessel(dto);
    }

    @GetMapping("/speed")
    public ResponseEntity<List<Vessel>> getVessel(@RequestParam Double speed){
        return vesselService.getVesselBySpeed(speed);
    }

    @GetMapping
    public ResponseEntity<List<Vessel>> getVessel(){
        return vesselService.getVessel();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Vessel> getVessel(@PathVariable("id") Long id){

        return vesselService.getVessel(id);
    }

    @PutMapping("/id")
    public ResponseEntity<Vessel> updateVessel(@RequestParam Long id,
                                               @RequestBody VesselDTO dto){
        return vesselService.updateVessel(dto, id);
    }
    @PutMapping("/name")
    public ResponseEntity<Vessel> updateVessel(@RequestParam String name,
                                               @RequestBody VesselDTO dto){
        return vesselService.updateVessel(dto, name);
    }


}
