package com.vt.vtserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.metrics.export.prometheus.EnablePrometheusMetrics;

@SpringBootApplication
public class VtServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VtServerApplication.class, args);
    }

}
