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
@Table(name = "stationary_object")
public class StationaryObject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stationary_object_sequence")  //???
    @SequenceGenerator(name="stationary_object_sequence", sequenceName = "sequence_generator", allocationSize=1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;

    @Column(name = "creation_time")
    private Timestamp creationTime;

    @Column(name = "update_time")
    private Timestamp updateTime;

    @Column(name = "deleted")
    private Boolean deleted;
}
