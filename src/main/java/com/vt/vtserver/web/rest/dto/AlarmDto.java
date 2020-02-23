package com.vt.vtserver.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@AllArgsConstructor
@Data
public class AlarmDto {
    private Long vesselTrackNumber;
    private Long collisionObjectId;

    //Time in seconds until collision
    private Timestamp collisionTime;
    //Minimal range between vessel and collision object
    private Double minimalRange;
}
