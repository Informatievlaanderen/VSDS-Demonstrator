package be.informatievlaanderen.vsds.demonstrator.member.domain.member.repositories;

import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    void saveMember(Member geometry);
    List<Member> getMembersByGeometry(Geometry geometry, String collectionName, LocalDateTime startTime, LocalDateTime endTime);
    Optional<Member> findByMemberId(String memberId);
    long getNumberOfMembers();
    List<Member> findMembersByCollectionAfterLocalDateTime(String collection, LocalDateTime localDateTime);

    long getNumberOfMembersByCollection(String collection);
}
