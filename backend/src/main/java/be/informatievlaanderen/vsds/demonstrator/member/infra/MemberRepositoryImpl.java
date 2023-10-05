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
        memberGeometryJpaRepo.save(new MemberEntity(member.getMemberId(), member.getCollection(), member.getGeometry(), member.getTimestamp()));
    }

    @Override
    public List<Member> getMembersByGeometry(Geometry geometry, String collectionName, LocalDateTime startTime, LocalDateTime endTime) {
        return memberGeometryJpaRepo
                .getMemberGeometryEntitiesCoveredByGeometryInTimePeriodAndCollection(geometry, collectionName, startTime, endTime)
                .stream()
                .map(mapEntityToMember())
                .toList();
    }

    @Override
    public Optional<Member> findByMemberId(String memberId) {
        return memberGeometryJpaRepo
                .findById(memberId)
                .map(mapEntityToMember());
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
                .map(mapEntityToMember())
                .toList();
    }

    @Override
    public long getNumberOfMembersByCollection(String collection) {
        return memberGeometryJpaRepo.countAllByCollection(collection);
    }

    private Function<MemberEntity, Member> mapEntityToMember() {
        return entity -> new Member(entity.getMemberId(), entity.getCollection(), entity.getGeometry(), entity.getTimestamp());
    }
}
