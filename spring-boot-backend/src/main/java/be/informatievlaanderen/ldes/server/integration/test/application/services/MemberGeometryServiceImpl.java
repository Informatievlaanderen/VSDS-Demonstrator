package be.informatievlaanderen.ldes.server.integration.test.application.services;

import be.informatievlaanderen.ldes.server.integration.test.application.config.StreamsConfig;
import be.informatievlaanderen.ldes.server.integration.test.application.exceptions.InvalidGeometryProvidedException;
import be.informatievlaanderen.ldes.server.integration.test.application.valueobjects.MemberDTO;
import be.informatievlaanderen.ldes.server.integration.test.application.valueobjects.MemberGeometryDto;
import be.informatievlaanderen.ldes.server.integration.test.application.exceptions.ResourceNotFoundException;
import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.repositories.MemberGeometryRepository;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.springframework.stereotype.Service;
import org.wololo.jts2geojson.GeoJSONWriter;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberGeometryServiceImpl implements MemberGeometryService {
    private final GeoJSONWriter geoJSONWriter = new GeoJSONWriter();
    private final MemberGeometryRepository repo;
    private final StreamsConfig streams;

    public MemberGeometryServiceImpl(MemberGeometryRepository repo, StreamsConfig streams) {
        this.repo = repo;
        this.streams = streams;
    }

    @Override
    public void ingestMemberGeometry(MemberDTO memberDTO) {
        try {
            repo.saveMember(memberDTO.getMemberGeometry(streams.getStreams()));
        } catch (FactoryException | TransformException e) {
            throw new InvalidGeometryProvidedException(memberDTO.getModel(), e);
        }
    }

    @Override
    public List<MemberGeometryDto> getMembersInRectangle(Geometry rectangleGeometry, LocalDateTime timestamp) {
        return repo.getMembersByGeometry(rectangleGeometry, timestamp).stream().map(memberGeometry -> new MemberGeometryDto(memberGeometry.getMemberId(), geoJSONWriter.write(memberGeometry.getGeometry()), memberGeometry.getTimestamp())).toList();
    }

    @Override
    public MemberGeometryDto getMemberById(String memberId) {
        return repo.findByMemberId(memberId)
                .map(memberGeometry -> new MemberGeometryDto(memberGeometry.getMemberId(), geoJSONWriter.write(memberGeometry.getGeometry()), memberGeometry.getTimestamp()))
                .orElseThrow(() -> new ResourceNotFoundException("MemberGeometry", memberId));
    }
}
