package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import be.informatievlaanderen.vsds.demonstrator.member.application.config.StreamsConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.InvalidGeometryProvidedException;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.IngestedMemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.MemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.ResourceNotFoundException;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.repositories.MemberRepository;
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
    private final MemberRepository repo;
    private final StreamsConfig streams;

    public MemberGeometryServiceImpl(MemberRepository repo, StreamsConfig streams) {
        this.repo = repo;
        this.streams = streams;
    }

    @Override
    public void ingestMemberGeometry(IngestedMemberDto ingestedMemberDto) {
        try {
            repo.saveMember(ingestedMemberDto.getMemberGeometry(streams.getStreams()));
        } catch (FactoryException | TransformException e) {
            throw new InvalidGeometryProvidedException(ingestedMemberDto.getModel(), e);
        }
    }

    @Override
    public List<MemberDto> getMembersInRectangle(Geometry rectangleGeometry, LocalDateTime timestamp) {
        return repo.getMembersByGeometry(rectangleGeometry, timestamp).stream().map(memberGeometry -> new MemberDto(memberGeometry.getMemberId(), geoJSONWriter.write(memberGeometry.getGeometry()), memberGeometry.getTimestamp())).toList();
    }

    @Override
    public MemberDto getMemberById(String memberId) {
        return repo.findByMemberId(memberId)
                .map(memberGeometry -> new MemberDto(memberGeometry.getMemberId(), geoJSONWriter.write(memberGeometry.getGeometry()), memberGeometry.getTimestamp()))
                .orElseThrow(() -> new ResourceNotFoundException("MemberGeometry", memberId));
    }
}
