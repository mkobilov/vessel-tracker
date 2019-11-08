package com.vts.vtsserver.model;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vessel_sequence")  //???
    @SequenceGenerator(name="vessel_sequence", sequenceName = "sequence_generator", allocationSize=1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "heading")
    private Double heading;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;

    @Column(name = "creation_time")
    private Timestamp creationTime;

    @Column(name = "update_time")
    private Timestamp updateTime;

    @Column(name = "deleted", columnDefinition = "boolean default false")
    private Boolean deleted = false;

}
