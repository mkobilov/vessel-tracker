package com.vt.vtserver.model;


import com.vt.vtserver.web.rest.dto.TargetDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Data
@Slf4j
@NoArgsConstructor
@Table(name = "target")
public class Target {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "target_sequence")
    @SequenceGenerator(name = "target_sequence", sequenceName = "target_sequence_generator", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Column(name = "track_nb")
    private Long trackNumber;

    private Double x;
    private Double y;
    private Double vx;
    private Double vy;
    private Double heading;
    private Double speed;

    @Column(name = "sac")
    private int systemAreaCode;
    @Column(name = "sin")
    private int systemIdentificationCode;

    private OffsetDateTime dateTime;
    private double lat;
    private double lon;

    @Column(name = "cfn")
    private int confirmationType;
    @Column(name = "fpc")
    private int correlationType;
    @Column(name = "mon")
    private int sensorType;
    @Column(name = "mrh")
    private int mostReliableHeight;
    @Column(name = "sim")
    private int trackRealityType;
    @Column(name = "spi")
    private int specialPositionIdentification;
    @Column(name = "src")
    private int sourceOfCalculatedTrackAltitude;
    @Column(name = "tse")
    private int transmissionOrder;

    //crutch for grafana worldmap plugin
    @Column(name = "stationary_object")
    private short stationaryObject;

    @Column(name = "creation_time")
    private OffsetDateTime creationTime;
    @Column(name = "update_time")
    private OffsetDateTime updateTime;

    public Target(TargetDto dto) {
        this.setConfirmationType(dto.getConfirmationType());
        this.setDateTime(dto.getDateTime());
        this.setCorrelationType(dto.getCorrelationType());
        this.setHeading(heading);
        this.setLat(dto.getLat());
        this.setLon(dto.getLon());
        this.setSensorType(dto.getSensorType());
        this.setMostReliableHeight(dto.getMostReliableHeight());
        this.setSystemAreaCode(dto.getSystemAreaCode());
        this.setTrackRealityType(dto.getTrackRealityType());
        this.setSystemIdentificationCode(dto.getSystemIdentificationCode());
        this.setSpecialPositionIdentification(dto.getSpecialPositionIdentification());
        this.setSourceOfCalculatedTrackAltitude(dto.getSourceOfCalculatedTrackAltitude());
        this.setTrackNumber(dto.getTrackNumber());
        this.setTransmissionOrder(dto.getTransmissionOrder());
        this.setVx((double) dto.getVx());
        this.setVy((double) dto.getVy());
        this.setDateTime(dto.getDateTime());
    }
}
