package be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.services;

import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.entities.MemberGeometry;
import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.MemberGeometryDto;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberGeometryService {
    void ingestMemberGeometry(MemberGeometry memberGeometry);

    List<MemberGeometryDto> getMembersInRectangle(Geometry rectangleGeometry, LocalDateTime timestamp);

    MemberGeometryDto getMemberById(String memberId);
}
