package com.vt.vtserver.web.rest;

import com.vt.vtserver.service.LogService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geotools.xml.xsi.XSISimpleTypes;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/log")
public class LogController{

    @Autowired
    MeterRegistry meterRegistry;

    private final LogService logService;

    @GetMapping("/error")
    public String getError(){
        log.error("Err message");
        return "Err message";
    }

    @GetMapping("/warning")
    public String getWarning(){
        log.warn("Warn message");
        return "Warning message";
    }

    @GetMapping("/info")
    public String getInfo(){
        log.info("Info message");
        return "info message";
    }

    @GetMapping("/container")
    public ResponseEntity<String> getThreads() throws InterruptedException {
        String s = new String("ok");
        logService.getThreads();
        return ResponseEntity.ok(s);
    }
}
