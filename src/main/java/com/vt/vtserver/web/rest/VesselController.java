package com.vt.vtserver.web.rest;

import com.vt.vtserver.model.Vessel;
import com.vt.vtserver.service.VesselService;
import com.vt.vtserver.web.rest.dto.VesselDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vessel")
public class VesselController {
    private final VesselService vesselService;

    @PostMapping
    public ResponseEntity<Vessel> postVessel(@RequestBody VesselDto dto) {

        Vessel vessel = vesselService.postVessel(dto);

        if (vessel == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

        return ResponseEntity.ok(vessel);
    }

    @GetMapping("/speed")
    public ResponseEntity<List<Vessel>> getVessel(@RequestParam Double speed) {

        List<Vessel> list = vesselService.getVesselBySpeed(speed);

        if (list.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

        return ResponseEntity.ok(list);
    }

    @GetMapping
    public ResponseEntity<List<Vessel>> getVessel() {

        List<Vessel> list = vesselService.getAll();

        if (list.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vessel> getVessel(@PathVariable("id") Long id) {

        Vessel vessel = vesselService.getVessel(id);

        if (Objects.isNull(vessel))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

        return ResponseEntity.ok(vessel);
    }

    @PutMapping("/id")
    public ResponseEntity<Vessel> updateVessel(@RequestParam Long id,
                                               @RequestBody VesselDto dto) {
        Vessel vessel = vesselService.updateVessel(dto, id);

        if (Objects.isNull(vessel))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

        return ResponseEntity.ok(vessel);
    }

    @PutMapping("/name")
    public ResponseEntity<Vessel> updateVessel(@RequestParam String name,
                                               @RequestBody VesselDto dto) {
        Vessel vessel = vesselService.updateVessel(dto, name);

        if (Objects.isNull(vessel))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

        return ResponseEntity.ok(vessel);
    }


}
