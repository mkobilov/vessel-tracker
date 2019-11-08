package com.vts.vtsserver.web.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/log")
public class LogController{

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


}
