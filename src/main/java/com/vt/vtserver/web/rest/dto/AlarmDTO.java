package com.vt.vtserver.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AlarmDTO {
    private Long vessel_id;
    private Long collision_object_id;

    //Time in seconds until collision
    private Double tmin;
    //Minimal range between vessel and collision object
    private Double rmin;
}
