package com.vt.vtserver.service;

import com.vt.vtserver.service.Asterix.CommonConstants;
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

    public boolean scanThreads = false;

    @Async
    public void scanThreads() throws InterruptedException {
        ArrayList<Thread> threads = new ArrayList<>(Thread.getAllStackTraces().keySet());
        List<Thread> threadList = threads.stream().
                filter(it -> it.getName().startsWith(CommonConstants.THREAD_NAME_PATTERN)).
                filter(it -> it.getState() == Thread.State.RUNNABLE).
                collect(Collectors.toList());

        String threadGaugeName = "thread_gauge";
        Gauge.builder(threadGaugeName, threadList, List::size).register(meterRegistry);
        while (scanThreads) {
            threadList.clear();
            threadList.addAll(threads.stream().filter(it -> it.getName().startsWith("container")).
                    filter(it -> it.getState() == Thread.State.RUNNABLE).
                    collect(Collectors.toList()));
            Thread.sleep(1000, 0);
        }
    }

}
