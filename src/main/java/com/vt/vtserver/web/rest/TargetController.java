package com.vt.vtserver.web.rest;

import com.vt.vtserver.model.Target;
import com.vt.vtserver.service.TargetService;
import com.vt.vtserver.web.rest.dto.TargetDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/target")
public class TargetController {

    private final TargetService targetService;

    @GetMapping
    public ResponseEntity<List<Target>> getAllTargets(){
        List<Target> list = targetService.getAllTargets();
        if(list != null)
            return ResponseEntity.ok(list);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @PostMapping
    public ResponseEntity<Target> postTarget(@RequestBody TargetDTO dto){
        Target target = targetService.postTarget(dto, null);

        if(target != null)
            return ResponseEntity.ok(target);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

}
