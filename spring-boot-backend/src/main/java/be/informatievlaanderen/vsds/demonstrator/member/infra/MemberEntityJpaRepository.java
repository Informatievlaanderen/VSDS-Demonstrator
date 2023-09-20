package be.informatievlaanderen.vsds.demonstrator.member.infra;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberEntityJpaRepository extends JpaRepository<MemberEntity, String> {

    @Query(value = "select l from MemberEntity l where intersects(l.geometry, :geometry) = true and l.timestamp = :timestamp")
    List<MemberEntity> getMemberGeometryEntitiesCoveredByGeometry(@Param("geometry") Geometry geometry, @Param("timestamp") LocalDateTime timestamp);
}
