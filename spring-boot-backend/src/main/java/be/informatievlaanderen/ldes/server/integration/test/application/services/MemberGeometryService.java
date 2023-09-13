package be.informatievlaanderen.ldes.server.integration.test.application.services;

import be.informatievlaanderen.ldes.server.integration.test.application.valueobjects.MemberDTO;
import be.informatievlaanderen.ldes.server.integration.test.application.valueobjects.MemberGeometryDto;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberGeometryService {
    void ingestMemberGeometry(MemberDTO memberDTO);

    List<MemberGeometryDto> getMembersInRectangle(Geometry rectangleGeometry, LocalDateTime timestamp);

    MemberGeometryDto getMemberById(String memberId);
}
