package com.vts.vtsserver.repository;

import com.vts.vtsserver.model.Vessel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VesselRepository extends JpaRepository<Vessel, Long> {
    List<Vessel> findByName(String name);
    List<Vessel> findBySpeedGreaterThan(Double speed);
}