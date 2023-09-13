package be.informatievlaanderen.ldes.server.integration.test.infra;

import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.entities.MemberGeometry;
import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.repositories.MemberGeometryRepository;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberGeometryRepositoryImpl implements MemberGeometryRepository {
    private final MemberGeometryJpaRepository memberGeometryJpaRepo;

    public MemberGeometryRepositoryImpl(MemberGeometryJpaRepository memberGeometryJpaRepo) {
        this.memberGeometryJpaRepo = memberGeometryJpaRepo;
    }

    @Override
    public void saveMember(MemberGeometry member) {
        memberGeometryJpaRepo.save(new MemberGeometryEntity(member.getMemberId(), member.getGeometry(), member.getTimestamp()));
    }

    @Override
    public List<MemberGeometry> getMembersByGeometry(Geometry geometry, LocalDateTime timestamp) {
        return memberGeometryJpaRepo
                .getMemberGeometryEntitiesCoveredByGeometry(geometry, timestamp)
                .stream()
                .map(entity -> new MemberGeometry(entity.getMemberId(), entity.getGeometry(), entity.getTimestamp()))
                .toList();
    }

    @Override
    public Optional<MemberGeometry> findByMemberId(String memberId) {
        return memberGeometryJpaRepo
                .findById(memberId)
                .map(entity -> new MemberGeometry(entity.getMemberId(), entity.getGeometry(), entity.getTimestamp()));
    }
}
