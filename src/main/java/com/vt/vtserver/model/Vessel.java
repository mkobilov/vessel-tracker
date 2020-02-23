package com.vt.vtserver.model;

import com.vt.vtserver.web.rest.dto.VesselDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Slf4j
@NoArgsConstructor
@Table(name = "vessel")
public class Vessel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vessel_sequence")
    @SequenceGenerator(name = "vessel_sequence", sequenceName = "sequence_generator", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    private String name;

    private String description;

    private Double heading;

    private Double speed;

    private Double lat;

    private Double lon;

    @Column(name = "creation_time")
    private Timestamp creationTime;

    @Column(name = "update_time")
    private Timestamp updateTime;

    @Column(columnDefinition = "boolean default false")
    private Boolean deleted = false;


    public Vessel(VesselDto dto) {
        this.setName(dto.getName());
        this.setDescription(dto.getDescription());
        this.setHeading(dto.getHeading());
        this.setSpeed(dto.getSpeed());
        this.setLat(dto.getLat());
        this.setLon(dto.getLon());
    }
}
