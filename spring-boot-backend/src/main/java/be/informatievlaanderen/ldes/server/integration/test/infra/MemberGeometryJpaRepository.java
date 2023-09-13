package be.informatievlaanderen.ldes.server.integration.test.infra;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberGeometryJpaRepository extends JpaRepository<MemberGeometryEntity, String> {

    @Query(value = "select l from MemberGeometryEntity l where intersects(l.geometry, :geometry) = true and l.timestamp = :timestamp")
    List<MemberGeometryEntity> getMemberGeometryEntitiesCoveredByGeometry(@Param("geometry") Geometry geometry, @Param("timestamp") LocalDateTime timestamp);
}
