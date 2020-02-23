package com.vt.vtserver.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public class StationaryObjectDto {

    private Long id;
    private String name;
    private String description;

    private Double lat;
    private Double lon;
    //crutch for worldmap grafana plugin
    private Long trackNumber;

    private Double x;
    private Double y;
}
