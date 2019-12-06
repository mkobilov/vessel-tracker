package com.vt.vtserver.model;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;


import javax.persistence.*;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

@Entity
@Data
@Slf4j
@Table(name = "target")
public class Target {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "target_sequence")
    @SequenceGenerator(name="target_sequence", sequenceName = "target_sequence_generator", allocationSize=1)
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

    private int sac;
    private int sin;
    private OffsetDateTime dateTime;
    private double lat;
    private double lon;


    private int cfn;
    private int fpc;
    private int mon;
    private int mrh;
    private int sim;
    private int spi;
    private int src;
    private int tse;


    @Column(name = "creation_time")
    private OffsetDateTime creationTime;
    @Column(name = "update_time")
    private OffsetDateTime updateTime;
}
