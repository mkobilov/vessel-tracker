package com.vt.vtserver.web.rest.dto;


import lombok.Data;


@Data
public class VesselDTO {
    private String name;
    private String description;
    private Double heading;
    private Double speed;
    private Double lat;
    private Double lon;
}
