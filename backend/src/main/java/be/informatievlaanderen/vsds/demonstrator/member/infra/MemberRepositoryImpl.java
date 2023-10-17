package be.informatievlaanderen.vsds.demonstrator.member.infra;

import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.repositories.MemberRepository;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
                .getMemberGeometryEntitiesCoveredByGeometryInTimePeriodAndCollection(geometry, collectionName, startTime, endTime)
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
                .findByCollectionAndTimestampAfter(collection,localDateTime)
                .stream()
                .map(MemberEntity::toDomainObject)
                .toList();
    }

    @Override
    public long getNumberOfMembersByCollection(String collection) {
        return memberGeometryJpaRepo.countAllByCollection(collection);
    }
}
