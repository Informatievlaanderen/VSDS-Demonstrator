package be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.repositories;

import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.entities.MemberGeometry;
import org.locationtech.jts.geom.Geometry;

import java.util.List;
import java.util.Optional;

public interface MemberGeometryRepository {
    void saveMember(MemberGeometry geometry);
    List<MemberGeometry> getMembersByGeometry(Geometry geometry);
    Optional<MemberGeometry> findByMemberId(String memberId);
}
