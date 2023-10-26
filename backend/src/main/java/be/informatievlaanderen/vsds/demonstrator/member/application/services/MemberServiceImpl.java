package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import be.informatievlaanderen.vsds.demonstrator.member.application.config.EventStreamConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.config.StreamsConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.InvalidGeometryProvidedException;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.MissingCollectionException;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.ResourceNotFoundException;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.IngestedMemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.MemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.valueobjects.Dataset;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.valueobjects.HourCount;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.valueobjects.LineChart;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.repositories.MemberRepository;
import be.informatievlaanderen.vsds.demonstrator.member.rest.dtos.LineChartDto;
import be.informatievlaanderen.vsds.demonstrator.member.rest.websocket.MessageController;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.wololo.jts2geojson.GeoJSONWriter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            EventStreamConfig eventStreamConfig = streams.getStream(ingestedMemberDto.getCollection())
                    .orElseThrow(() -> new MissingCollectionException(ingestedMemberDto.getCollection()));
            Member member = ingestedMemberDto.getMember(eventStreamConfig);
            repository.saveMember(member);
            MemberDto memberDto = new MemberDto(member.getMemberId(), geoJSONWriter.write(member.getGeometry()), ingestedMemberDto.getCollection(), member.getTimestamp(), member.getIsVersionOf(), member.getProperties());
            messageController.send(memberDto);

            log.info("new member ingested");
        } catch (FactoryException | TransformException e) {
            throw new InvalidGeometryProvidedException(ingestedMemberDto.getModel(), e);
        }
    }

    @Override
    public List<MemberDto> getMembersInRectangle(Geometry rectangleGeometry, String collectionName, LocalDateTime timestamp, String timePeriod) {
        return repository.getMembersByGeometry(rectangleGeometry, collectionName, LocalDateTime.now().minusDays(7), timestamp)
                .stream()
                .collect(Collectors.groupingBy(Member::getIsVersionOf, Collectors.maxBy(Comparator.comparing(Member::getTimestamp))))
                .values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(memberGeometry -> new MemberDto(memberGeometry.getMemberId(), geoJSONWriter.write(memberGeometry.getGeometry()), collectionName, memberGeometry.getTimestamp(), memberGeometry.getIsVersionOf(), memberGeometry.getProperties()))
                .toList();
    }

    @Override
    public MemberDto getMemberById(String memberId) {
        return repository.findByMemberId(memberId)
                .map(memberGeometry -> new MemberDto(memberGeometry.getMemberId(), geoJSONWriter.write(memberGeometry.getGeometry()), memberGeometry.getCollection(), memberGeometry.getTimestamp(), memberGeometry.getIsVersionOf(), memberGeometry.getProperties()))
                .orElseThrow(() -> new ResourceNotFoundException("Member", memberId));
    }

    @Override
    public long getNumberOfMembers() {
        return repository.getNumberOfMembers();
    }

    @Override
    public long getNumberOfMembersByCollection(String collection) {
        return repository.getNumberOfMembersByCollection(collection);
    }

    @Override
    public LineChartDto getLineChartDtos() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LineChart lineChart = new LineChart(startDate);

        streams
                .getStreams()
                .keySet()
                .stream()
                .map(collection -> {
                    long numberOfMembers = getNumberOfMembersByCollection(collection);

                    List<Member> membersAfterLocalDateTime = repository.findMembersByCollectionAfterLocalDateTime(collection, startDate);
                    return new Dataset(collection, numberOfMembers - membersAfterLocalDateTime.size(), new HourCount(membersAfterLocalDateTime));
                }).forEach(lineChart::addDataSet);

        return new LineChartDto(lineChart.getLabels(), lineChart.getDatasetDtos());
    }
}
