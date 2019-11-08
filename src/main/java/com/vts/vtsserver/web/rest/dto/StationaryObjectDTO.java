package com.vts.vtsserver.web.rest.dto;

import lombok.Data;


@Data
public class StationaryObjectDTO {
    private String name;
    private String description;
    private Double lat;
    private Double lon;
}