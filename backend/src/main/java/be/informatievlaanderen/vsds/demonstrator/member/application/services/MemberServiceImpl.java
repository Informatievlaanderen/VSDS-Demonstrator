package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import be.informatievlaanderen.vsds.demonstrator.member.application.config.StreamsConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.InvalidGeometryProvidedException;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.ResourceNotFoundException;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.IngestedMemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.MemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.repositories.MemberRepository;
import be.informatievlaanderen.vsds.demonstrator.member.rest.websocket.MessageController;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final MessageController messageController;
    private static final Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);


    public MemberServiceImpl(MemberRepository repository, StreamsConfig streams, MessageController messageController) {
        this.repository = repository;
        this.streams = streams;
        this.messageController = messageController;
    }

    @Override
    public void ingestMember(IngestedMemberDto ingestedMemberDto) {
        try {
            Member member = ingestedMemberDto.getMemberGeometry(streams.getStreams());
            repository.saveMember(member);
            messageController.send(new MemberDto(member.getMemberId(), geoJSONWriter.write(member.getGeometry()), member.getTimestamp()));

            log.info("new member ingested");
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

    @Override
    public long getNumberOfMembers() {
        return repository.getNumberOfMembers();
    }
}
