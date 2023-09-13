package be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities;


import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;

public class Member {
    private final String memberId;
    private final Geometry geometry;
    private final LocalDateTime timestamp;

    public Member(String memberId, Geometry geometry, LocalDateTime timestamp) {
        this.memberId = memberId;
        this.geometry = geometry;
        this.timestamp = timestamp;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member that = (Member) o;

        return memberId.equals(that.memberId);
    }

    @Override
    public int hashCode() {
        return memberId.hashCode();
    }
}
