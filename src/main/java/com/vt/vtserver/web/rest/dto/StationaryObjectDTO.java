package com.vt.vtserver.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public class StationaryObjectDTO {
    Long id;
    String name;
    String description;

    Double lat;
    Double lon;

    Double x;
    Double y;

}
