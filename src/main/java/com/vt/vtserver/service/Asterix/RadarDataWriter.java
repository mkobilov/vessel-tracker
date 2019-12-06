package com.vt.vtserver.service.Asterix;

import com.vt.vtserver.model.Target;
import com.vt.vtserver.repository.TargetRepository;
import com.vt.vtserver.web.rest.dto.TargetDTO;
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

import java.time.OffsetDateTime;

public class RadarDataWriter {

    private final double ASTERIX_VELOCITY_RATIO = 0.25;     //Velocity data inside asterix frame should be multiplied
                                                            //by this number to get value in m/s
    private final TargetRepository radarTargetRepository;
    private CoordinateReferenceSystem sourceCRS;
    private CoordinateReferenceSystem targetCRS;
    private MathTransform mathTransform;
    GeometryFactory geometryFactory;

    public RadarDataWriter(TargetRepository radarTargetRepository) throws FactoryException {
        this.radarTargetRepository = radarTargetRepository;
        this.sourceCRS = CRS.decode("EPSG:4326");   //code of elliptical reference system
        this.targetCRS = CRS.decode("EPSG:3857");   //code of Cartesian reference system
        this.mathTransform = CRS.findMathTransform(sourceCRS, targetCRS);
        this.geometryFactory = JTSFactoryFinder.getGeometryFactory();
    }

    public void writeRadar(TargetDTO dto, OffsetDateTime creationTime) throws TransformException {
        int vX = dto.getVx();
        int vY = dto.getVy();

        double dVx = ((double)vX)*ASTERIX_VELOCITY_RATIO;
        double dVy = ((double)vY)*ASTERIX_VELOCITY_RATIO;

        double v = Math.sqrt(dVx*dVx + dVy*dVy);
        double heading;
        heading = Math.atan2(dVy, dVx)*180/Math.PI;

        Point sourcePoint = geometryFactory.createPoint(new Coordinate(dto.getLon(), dto.getLat()));
        sourcePoint.setSRID(4326);
        Geometry geometry = JTS.transform(sourcePoint,mathTransform);
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


        target.setCreationTime(creationTime);
        System.out.println(target);
        radarTargetRepository.save(target);
    }
}
