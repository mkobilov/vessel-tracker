package com.vt.vtserver.repository;

import com.vt.vtserver.model.Alarm;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;

@EntityScan(basePackages = {"com.vt.vtserver.model"})
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    void deleteByVesselTrackNumberAndCollisionObjectId(Long targteId, Long objectId);
}
