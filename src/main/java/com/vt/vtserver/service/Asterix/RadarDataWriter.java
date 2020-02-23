package com.vt.vtserver.service.Asterix;

import com.vt.vtserver.config.ApplicationProperties;
import com.vt.vtserver.model.Target;
import com.vt.vtserver.repository.TargetRepository;
import com.vt.vtserver.service.Messaging.MessageUnit;
import com.vt.vtserver.web.rest.dto.TargetDto;
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

    private static final double ASTERIX_VELOCITY_RATIO = 0.25;     //Velocity data inside asterix frame should be multiplied
    //by this number to get value in m/s
    private final TargetRepository radarTargetRepository;
    private CoordinateReferenceSystem sourceCRS;
    private CoordinateReferenceSystem targetCRS;
    private MathTransform mathTransform;
    private GeometryFactory geometryFactory;

    private static final int ellipticalSpecialReferenceIdentifier = 4326;

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

    public void writeRadar(TargetDto dto, OffsetDateTime creationTime) throws TransformException {
        int vX = dto.getVx();
        int vY = dto.getVy();

        double dVx = ((double) vX) * ASTERIX_VELOCITY_RATIO;
        double dVy = ((double) vY) * ASTERIX_VELOCITY_RATIO;

        double v = Math.sqrt(dVx * dVx + dVy * dVy);
        double heading = Math.atan2(dVy, dVx) * CommonConstants.DEGREES_180 / Math.PI;
        Point sourcePoint = geometryFactory.createPoint(new Coordinate(dto.getLat(), dto.getLon()));
        sourcePoint.setSRID(ellipticalSpecialReferenceIdentifier);
        Geometry geometry = JTS.transform(sourcePoint, mathTransform);
        double x = geometry.getCoordinate().getX();
        double y = geometry.getCoordinate().getY();

        Target target = new Target(dto);
        target.setSpeed(v);
        target.setVx(dVx);
        target.setVy(dVy);
        target.setX(x);
        target.setY(y);
        target.setHeading(heading);

        MessageUnit messageUnit = new MessageUnit(target.getTrackNumber(), target.getX(), target.getVx(),
                target.getY(), target.getVy());
        rabbitTemplate.convertAndSend(applicationProperties.getQueue(), messageUnit);

        target.setCreationTime(creationTime);
        radarTargetRepository.save(target);
    }
}
