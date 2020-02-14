package com.vt.vtserver.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "stationary_object")
@Data
public class StationaryObject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stationary_object_sequence")
    @SequenceGenerator(name="stationary_object_sequence", sequenceName = "stationary_object_sequence_generator", allocationSize=1)
    @Column(name = "id")
    private Long id;

    private String name;
    private String description;

    private Double x;
    private Double y;

    private Double lat;
    private Double lon;


    @Column(columnDefinition = "boolean default false")
    private Boolean deleted = false;

    public StationaryObject(){

    }
}
