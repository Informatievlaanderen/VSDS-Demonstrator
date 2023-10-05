package be.informatievlaanderen.vsds.demonstrator.member.infra;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberEntityJpaRepository extends JpaRepository<MemberEntity, String> {

    @Query(value = "select l from member_entity l where intersects(l.geometry, :geometry) = true and l.timestamp >= :startTime and l.timestamp <= :endTime and l.collection = :collectionName")
    List<MemberEntity> getMemberGeometryEntitiesCoveredByGeometryInTimePeriod(@Param("geometry") Geometry geometry, @Param("collectionName") String collectionName, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    List<MemberEntity> findByTimestampAfter(LocalDateTime localDateTime);

}
