package com.vt.vtserver.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TargetDTO {


    int sac;
    int sin;
    OffsetDateTime dateTime;
    double lat;
    double lon;
    int latitudeWsg84;
    int longtitudeWsg84;
    int vx;
    int vy;
    Long trackNumber;
    int cfn;
    int fpc;
    int mon;
    int mrh;
    int sim;
    int spi;
    int src;
    int tse;


}
