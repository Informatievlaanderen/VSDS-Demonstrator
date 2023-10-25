package be.informatievlaanderen.vsds.demonstrator.member.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberEntityJpaRepository extends JpaRepository<MemberEntity, String> {

    @Query(value = "select l from member_entity l where l.timestamp >= :startTime and l.timestamp <= :endTime and l.collection = :collectionName")
    List<MemberEntity> getMemberGeometryEntitiesCoveredByGeometryInTimePeriodAndCollection(@Param("collectionName") String collectionName, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    List<MemberEntity> findByCollectionAndTimestampAfter(String collection, LocalDateTime localDateTime);

    long countAllByCollection(String collection);

}
