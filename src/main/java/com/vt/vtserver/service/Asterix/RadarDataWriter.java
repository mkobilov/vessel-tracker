package com.vt.vtserver.service.Asterix;

import com.vt.vtserver.config.ApplicationProperties;
import com.vt.vtserver.model.Target;
import com.vt.vtserver.repository.TargetRepository;
import com.vt.vtserver.service.Messaging.MessageUnit;
import com.vt.vtserver.web.rest.dto.TargetDTO;
import lombok.extern.slf4j.Slf4j;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.OffsetDateTime;

@Slf4j
public class RadarDataWriter {
    private final RabbitTemplate rabbitTemplate;
    private final ApplicationProperties applicationProperties;

    private final double ASTERIX_VELOCITY_RATIO = 0.25;     //Velocity data inside asterix frame should be multiplied
    //by this number to get value in m/s
    private final TargetRepository radarTargetRepository;
    private CoordinateReferenceSystem sourceCRS;
    private CoordinateReferenceSystem targetCRS;
    private MathTransform mathTransform;
    GeometryFactory geometryFactory;

    public RadarDataWriter(TargetRepository radarTargetRepository,
                           RabbitTemplate rabbitTemplate,
                           ApplicationProperties applicationProperties) throws FactoryException {
        this.radarTargetRepository = radarTargetRepository;
        this.sourceCRS = CRS.decode("EPSG:4326");   //code of elliptical reference system
        this.targetCRS = CRS.decode("EPSG:3857");   //code of Cartesian reference system
        this.mathTransform = CRS.findMathTransform(sourceCRS, targetCRS);
        this.geometryFactory = JTSFactoryFinder.getGeometryFactory();
        this.rabbitTemplate = rabbitTemplate;
        this.applicationProperties = applicationProperties;
    }

    public void writeRadar(TargetDTO dto, OffsetDateTime creationTime) throws TransformException {
        int vX = dto.getVx();
        int vY = dto.getVy();

        double dVx = ((double) vX) * ASTERIX_VELOCITY_RATIO;
        double dVy = ((double) vY) * ASTERIX_VELOCITY_RATIO;

        double v = Math.sqrt(dVx * dVx + dVy * dVy);
        double heading;
        heading = Math.atan2(dVy, dVx) * 180 / Math.PI;
        //TODO MB ERR HERE CHECK WITH REAL DATA
        Point sourcePoint = geometryFactory.createPoint(new Coordinate( dto.getLat(), dto.getLon()));
        sourcePoint.setSRID(4326);
        Geometry geometry = JTS.transform(sourcePoint, mathTransform);
        double x = geometry.getCoordinate().getX();
        double y = geometry.getCoordinate().getY();

        Target target = new Target();
        target.setCfn(dto.getCfn());
        target.setDateTime(dto.getDateTime());
        target.setFpc(dto.getFpc());
        target.setHeading(heading);
        target.setLat(dto.getLat());
        target.setLon(dto.getLon());
        target.setMon(dto.getMon());
        target.setMrh(dto.getMrh());
        target.setSac(dto.getSac());
        target.setSim(dto.getSim());
        target.setSin(dto.getSin());
        target.setSpeed(v);
        target.setSpi(dto.getSpi());
        target.setSrc(dto.getSrc());
        target.setTrackNumber(dto.getTrackNumber());
        target.setTse(dto.getTse());
        target.setVx(dVx);
        target.setVy(dVy);

        target.setX(x);
        target.setY(y);

        MessageUnit messageUnit = new MessageUnit(target.getTrackNumber(), target.getX(), target.getVx(),
                target.getY(), target.getVy());
        rabbitTemplate.convertAndSend(applicationProperties.getQueue(), messageUnit);

        target.setCreationTime(creationTime);
        radarTargetRepository.save(target);
    }
}
