package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import be.informatievlaanderen.vsds.demonstrator.member.application.config.StreamsConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.InvalidGeometryProvidedException;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.ResourceNotFoundException;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.IngestedMemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.MemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.repositories.MemberRepository;
import be.informatievlaanderen.vsds.demonstrator.member.rest.websocket.WebSocketConfig;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.springframework.stereotype.Service;
import org.wololo.jts2geojson.GeoJSONWriter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {
    private final GeoJSONWriter geoJSONWriter = new GeoJSONWriter();
    private final MemberRepository repository;
    private final StreamsConfig streams;

    public MemberServiceImpl(MemberRepository repository, StreamsConfig streams) {
        this.repository = repository;
        this.streams = streams;
    }

    @Override
    public void ingestMember(IngestedMemberDto ingestedMemberDto) {
        try {
            Member member = ingestedMemberDto.getMemberGeometry(streams.getStreams());
            repository.saveMember(member);
            WebSocketConfig.send(new MemberDto(member.getMemberId(), geoJSONWriter.write(member.getGeometry()), member.getTimestamp()));

        } catch (FactoryException | TransformException e) {
            throw new InvalidGeometryProvidedException(ingestedMemberDto.getModel(), e);
        }
    }

    @Override
    public List<MemberDto> getMembersInRectangle(Geometry rectangleGeometry, LocalDateTime timestamp, String timePeriod) {
        var duration = Duration.parse(timePeriod).dividedBy(2);
        return repository.getMembersByGeometry(rectangleGeometry, timestamp.minus(duration), timestamp.plus(duration))
                .stream()
                .map(memberGeometry -> new MemberDto(memberGeometry.getMemberId(), geoJSONWriter.write(memberGeometry.getGeometry()), memberGeometry.getTimestamp()))
                .toList();
    }

    @Override
    public MemberDto getMemberById(String memberId) {
        return repository.findByMemberId(memberId)
                .map(memberGeometry -> new MemberDto(memberGeometry.getMemberId(), geoJSONWriter.write(memberGeometry.getGeometry()), memberGeometry.getTimestamp()))
                .orElseThrow(() -> new ResourceNotFoundException("Member", memberId));
    }
}
