package com.vt.vtserver.web.rest;

import com.vt.vtserver.service.LogService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/log")
public class LogController {

    @Autowired
    MeterRegistry meterRegistry;

    private final LogService logService;

    @GetMapping("/error")
    public String getError() {
        log.error("Err message");
        return "Err message";
    }

    @GetMapping("/warning")
    public String getWarning() {
        log.warn("Warn message");
        return "Warning message";
    }

    @GetMapping("/info")
    public String getInfo() {
        log.info("Info message");
        return "info message";
    }

    @GetMapping("/container/start")
    public ResponseEntity<String> startThreadScan() throws InterruptedException {
        logService.scanThreads();
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/container/stop")
    public ResponseEntity<String> stopThreadScan() {
        logService.scanThreads = false;
        return ResponseEntity.ok("ok");
    }
}
