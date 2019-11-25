package com.vt.vtserver.model;


import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;

@Data
@Slf4j
public class Target {

    private Long track_number;
    private Timestamp time;
    private Double lat;
    private Double lon;
    private Double vx;
    private Double vy;
    private Double heading;
    private Double speed;
    private Bool status;

}
