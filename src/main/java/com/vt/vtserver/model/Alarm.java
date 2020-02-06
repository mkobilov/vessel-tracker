package com.vt.vtserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "alarm")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vessel_sequence")
    @SequenceGenerator(name = "vessel_sequence", sequenceName = "sequence_generator", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Column(name = "vessel_track_number")
    private Long vesselTrackNumber;
    @Column(name = "collision_object_id")
    private Long collisionObjectId;

    //Time in seconds until collision
    @Column(name = "time_of_collision")
    private Timestamp collisionTime;
    //Minimal range between vessel and collision object
    @Column(name = "minimal_range")
    private Double rmin;
}
