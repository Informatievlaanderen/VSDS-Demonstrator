package be.informatievlaanderen.vsds.demonstrator.member.infra;

import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.repositories.MemberRepository;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.RetentionPolicyConstants.MAX_AGE_OF_MEMBERS_IN_DAYS;

@Repository
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberEntityJpaRepository memberGeometryJpaRepo;

    public MemberRepositoryImpl(MemberEntityJpaRepository memberGeometryJpaRepo) {
        this.memberGeometryJpaRepo = memberGeometryJpaRepo;
    }

    @Override
    public void saveMember(Member member) {
        memberGeometryJpaRepo.save(MemberEntity.fromDomainObject(member));
    }

    @Override
    public List<Member> getMembersByGeometry(Geometry geometry, String collectionName, LocalDateTime startTime, LocalDateTime endTime) {
        return memberGeometryJpaRepo
                .getMemberGeometryEntitiesCoveredByGeometryInTimePeriodAndCollection(collectionName, startTime, endTime)
                .stream()
                .map(MemberEntity::toDomainObject)
                .toList();
    }

    @Override
    public Optional<Member> findByMemberId(String memberId) {
        return memberGeometryJpaRepo
                .findById(memberId)
                .map(MemberEntity::toDomainObject);
    }

    @Override
    public long getNumberOfMembers() {
        return memberGeometryJpaRepo.count();
    }

    @Override
    public List<Member> findMembersByCollectionAfterLocalDateTime(String collection, LocalDateTime localDateTime) {
        return memberGeometryJpaRepo
                .findByCollectionAndTimestampAfter(collection, localDateTime)
                .stream()
                .map(MemberEntity::toDomainObject)
                .toList();
    }

    @Override
    public long getNumberOfMembersByCollection(String collection) {
        return memberGeometryJpaRepo.countAllByCollection(collection);
    }

    @Override
    public void deleteMembersOlderThenSevenDays() {
        memberGeometryJpaRepo.deleteAllByTimestampBefore(LocalDateTime.now().minusDays(MAX_AGE_OF_MEMBERS_IN_DAYS));
    }
}
