package com.vt.vtserver.repository;

import com.vt.vtserver.model.Target;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TargetRepository extends JpaRepository<Target, Long>{
    @Query(value = "select * from target a " +
            "inner join " +
            "(select track_nb, max(creation_time) max " +
            "from target " +
            "where (creation_time > (now() - ( :m ||' seconds')\\:\\:interval)) " +
            "group by track_nb) b " +
            "on a.track_nb=b.track_nb and a.creation_time=b.max", nativeQuery = true)
    List<Target> getLatestTargetPositions(@Param("m") Integer seconds);
}
