package com.vt.vtserver.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/log")
public class LogController{

    @Autowired
    SimpleMessageListenerContainer container;

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
    public void setConsumers(@RequestParam int n){
        ArrayList<Thread> threads = new ArrayList<>(Thread.getAllStackTraces().keySet());
        //todo to gauge
        log.info(threads.stream().filter(it -> it.getName().startsWith("container")).
                filter(it -> it.getState() == Thread.State.RUNNABLE).
                collect(Collectors.toList()).toString());
    }
}
