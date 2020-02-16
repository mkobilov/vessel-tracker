package com.vt.vtserver.service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LogService {
    @Autowired
    MeterRegistry meterRegistry;

    @Async
    public void getThreads() throws InterruptedException {
        ArrayList<Thread> threads = new ArrayList<>(Thread.getAllStackTraces().keySet());
        //todo to gauge
        List<Thread> threadList = threads.stream().filter(it -> it.getName().startsWith("container")).
                filter(it -> it.getState() == Thread.State.RUNNABLE).
                collect(Collectors.toList());
        Gauge gauge = Gauge.builder("thread_gauge",threadList, List::size).register(meterRegistry);
        log.warn("size1 = " + threadList.size());

        while (true) {
            threadList.clear();
            threadList.addAll(threads.stream().filter(it -> it.getName().startsWith("container")).
                    filter(it -> it.getState() == Thread.State.RUNNABLE).
                    collect(Collectors.toList()));
            Thread.sleep(1000,0);
        }
    }

}
