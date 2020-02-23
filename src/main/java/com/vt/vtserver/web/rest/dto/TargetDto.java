package com.vt.vtserver.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class TargetDto {
    private int systemAreaCode;
    private int systemIdentificationCode;

    private OffsetDateTime dateTime;
    private double lat;
    private double lon;
    private int latitudeWsg84;
    private int longitudeWsg84;
    private int vx;
    private int vy;
    private Long trackNumber;

    private int confirmationType;
    private int correlationType;
    private int sensorType;
    private int mostReliableHeight;
    private int trackRealityType;
    private int specialPositionIdentification;
    private int sourceOfCalculatedTrackAltitude;
    private int transmissionOrder;
}
