package be.informatievlaanderen.vsds.demonstrator.member.infra;

import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLHStoreType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;
import java.util.Map;

@Entity(name = "member_entity")
@Table(indexes = {
        @Index(columnList = "collection"),
        @Index(columnList = "timestamp")
})
public class MemberEntity {
    @Id
    private String memberId;
    private String collection;
    private Geometry geometry;
    private LocalDateTime timestamp;
    private String isVersionOf;
    @Type(value = PostgreSQLHStoreType.class)
    @Column(columnDefinition = "hstore")
    private Map<String, String> properties;

    public MemberEntity() {
    }

    public MemberEntity(String memberId, String collection, Geometry geometry, LocalDateTime timestamp, String isVersionOf, Map<String, String> properties) {
        this.memberId = memberId;
        this.collection = collection;
        this.geometry = geometry;
        this.timestamp = timestamp;
        this.isVersionOf = isVersionOf;
        this.properties = properties;
    }

    public String getMemberId() {
        return memberId;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getIsVersionOf() {
        return isVersionOf;
    }

    public String getCollection() {
        return collection;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public Member toDomainObject() {
        return new Member(memberId, collection, geometry, isVersionOf, timestamp, properties);
    }

    public static MemberEntity fromDomainObject(Member member) {
        return new MemberEntity(
                member.getMemberId(),
                member.getCollection(),
                member.getGeometry(),
                member.getTimestamp(),
                member.getIsVersionOf(),
                member.getProperties()
        );
    }
}
