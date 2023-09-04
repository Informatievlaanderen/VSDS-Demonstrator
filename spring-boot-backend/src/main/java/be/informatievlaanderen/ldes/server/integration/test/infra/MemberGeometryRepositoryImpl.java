package be.informatievlaanderen.ldes.server.integration.test.infra;

import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.entities.MemberGeometry;
import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.repositories.MemberGeometryRepository;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberGeometryRepositoryImpl implements MemberGeometryRepository {
    private final MemberGeometryJpaRepository memberGeometryJpaRepo;

    public MemberGeometryRepositoryImpl(MemberGeometryJpaRepository memberGeometryJpaRepo) {
        this.memberGeometryJpaRepo = memberGeometryJpaRepo;
    }

    @Override
    public void saveMember(MemberGeometry geometry) {
        memberGeometryJpaRepo.save(new MemberGeometryEntity(geometry.getMemberId(), geometry.getGeometry()));
    }

    @Override
    public List<MemberGeometry> getMembersByGeometry(Geometry geometry) {
        return memberGeometryJpaRepo
                .getMemberGeometryEntitiesCoveredByGeometry(geometry)
                .stream()
                .map(memberGeometryEntity -> new MemberGeometry(memberGeometryEntity.getMemberId(), memberGeometryEntity.getGeometry()))
                .toList();
    }

    @Override
    public Optional<MemberGeometry> findByMemberId(String memberId) {
        return memberGeometryJpaRepo
                .findById(memberId)
                .map(entity -> new MemberGeometry(entity.getMemberId(), entity.getGeometry()));
    }
}
