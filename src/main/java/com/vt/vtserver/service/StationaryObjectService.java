package com.vt.vtserver.service;

import com.vt.vtserver.model.StationaryObject;
import com.vt.vtserver.repository.StationaryObjectRepository;
import com.vt.vtserver.web.rest.dto.StationaryObjectDTO;
import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StationaryObjectService {
    private final StationaryObjectRepository stationaryObjectRepository;

    public List<StationaryObject> getAllStationaryObjects() {
        try {
            return stationaryObjectRepository.findAll();
        } catch (Exception e) {
            log.error("Service Error getVessel :" + e);
            return null;
        }
    }

    public StationaryObject postStationaryObject(StationaryObjectDTO dto) throws FactoryException, TransformException {
        StationaryObject stationaryObject = new StationaryObject();
        if(dto.getLat() != null && dto.getLon() != null){
            CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");   //code of elliptical reference system
            CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857");   //code of Cartesian reference system
            MathTransform mathTransform = CRS.findMathTransform(sourceCRS, targetCRS);
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

            stationaryObject.setId(dto.getId());
            stationaryObject.setName(dto.getName());
            stationaryObject.setDescription(dto.getDescription());
            stationaryObject.setLat(dto.getLat());
            stationaryObject.setLon(dto.getLon());

            Point sourcePoint  =geometryFactory.createPoint(new Coordinate(dto.getLon(), dto.getLat()));
            Geometry geometry = JTS.transform(sourcePoint,mathTransform);
            stationaryObject.setX(geometry.getCoordinate().x);
            stationaryObject.setY(geometry.getCoordinate().y);

            stationaryObjectRepository.save(stationaryObject);

            return stationaryObject;
        }
        else if(dto.getX() != null && dto.getY() != null){
            CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:3857");   //code of elliptical reference system
            CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");   //code of Cartesian reference system
            MathTransform mathTransform = CRS.findMathTransform(sourceCRS, targetCRS);
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

            stationaryObject.setId(dto.getId());
            stationaryObject.setName(dto.getName());
            stationaryObject.setDescription(dto.getDescription());
            stationaryObject.setX(dto.getX());
            stationaryObject.setY(dto.getY());

            Point sourcePoint  =geometryFactory.createPoint(new Coordinate(dto.getX(), dto.getY()));
            Geometry geometry = JTS.transform(sourcePoint,mathTransform);
            stationaryObject.setLon(geometry.getCoordinate().x);
            stationaryObject.setLat(geometry.getCoordinate().y);

            stationaryObjectRepository.save(stationaryObject);

            return stationaryObject;
        }
        else{
            log.warn("Empty dto during postStationaryObject: " + dto);
            return null;
        }

    }
}
