package be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.services;

import be.informatievlaanderen.ldes.server.integration.test.domain.exceptions.ResourceNotFoundException;
import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.entities.MemberGeometry;
import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.repositories.MemberGeometryRepository;
import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.MemberGeometryDto;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;
import org.wololo.jts2geojson.GeoJSONWriter;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberGeometryServiceImpl implements MemberGeometryService {
    private final GeoJSONWriter geoJSONWriter = new GeoJSONWriter();
    private final MemberGeometryRepository repo;

    public MemberGeometryServiceImpl(MemberGeometryRepository repo) {
        this.repo = repo;
    }

    @Override
    public void ingestMemberGeometry(MemberGeometry memberGeometry) {
        repo.saveMember(memberGeometry);
    }

    @Override
    public List<MemberGeometryDto> getMembersInRectangle(Geometry rectangleGeometry, LocalDateTime timestamp) {
        return repo.getMembersByGeometry(rectangleGeometry, timestamp)
                .stream()
                .map(memberGeometry -> new MemberGeometryDto(memberGeometry.getMemberId(), geoJSONWriter.write(memberGeometry.getGeometry()), memberGeometry.getTimestamp()))
                .toList();
    }

    @Override
    public MemberGeometryDto getMemberById(String memberId) {
        return repo.findByMemberId(memberId)
                .map(memberGeometry -> new MemberGeometryDto(memberGeometry.getMemberId(), geoJSONWriter.write(memberGeometry.getGeometry()), memberGeometry.getTimestamp()))
                .orElseThrow(() -> new ResourceNotFoundException("MemberGeometry", memberId));
    }
}
