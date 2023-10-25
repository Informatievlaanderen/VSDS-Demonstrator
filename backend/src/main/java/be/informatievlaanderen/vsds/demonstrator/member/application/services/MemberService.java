package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.IngestedMemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.MemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.rest.dtos.LineChartDto;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberService {
    void ingestMember(IngestedMemberDto ingestedMemberDto);

    List<MemberDto> getMembersInRectangle(Geometry rectangleGeometry, String collectionName, LocalDateTime timestamp);

    MemberDto getMemberById(String memberId);

    long getNumberOfMembers();
    long getNumberOfMembersByCollection(String collection);

    LineChartDto getLineChartDtos();
}
