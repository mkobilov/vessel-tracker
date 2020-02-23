package com.vt.vtserver.model;

import com.vt.vtserver.web.rest.dto.AlarmDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "alarm")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Alarm {

    public Alarm(AlarmDto dto) {
        this.setVesselTrackNumber(dto.getVesselTrackNumber());
        this.setCollisionObjectId(dto.getCollisionObjectId());
        this.setMinimalRange(dto.getMinimalRange());
        this.setCollisionTime(dto.getCollisionTime());
        setCreationTime(OffsetDateTime.now(ZoneOffset.UTC));
    }

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
    private Double minimalRange;

    @Column(name = "creation_time")
    private OffsetDateTime creationTime;
}
