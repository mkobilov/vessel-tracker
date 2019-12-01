package com.vt.vtserver.web.rest.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TargetDTO {
    private Timestamp dateTime;

    private Double lat;
    private Double lon;
    private Double x;
    private Double y;
    private Double vx;
    private Double vy;
    private Double heading;
    private Double speed;
}
