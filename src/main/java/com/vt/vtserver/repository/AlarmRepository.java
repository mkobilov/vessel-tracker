package com.vt.vtserver.repository;

import com.vt.vtserver.model.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> deleteByVesselTrackNumberAndCollisionObjectId(Long vesselTrackNumber, Long stationaryObjectId);
}
