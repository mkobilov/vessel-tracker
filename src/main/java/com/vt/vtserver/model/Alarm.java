package com.vt.vtserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "alarm")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vessel_sequence")
    @SequenceGenerator(name="vessel_sequence", sequenceName = "alarm_sequence_generator", allocationSize=1)
    @Column(name = "id")
    private Long id;

    private Long vessel_id;
    private Long collision_object_id;

    //Time in seconds until collision
    private Double tmin;
    //Minimal range between vessel and collision object
    private Double rmin;
}
