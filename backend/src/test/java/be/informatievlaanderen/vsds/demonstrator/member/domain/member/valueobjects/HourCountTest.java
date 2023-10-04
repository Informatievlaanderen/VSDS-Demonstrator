package be.informatievlaanderen.vsds.demonstrator.member.domain.member.valueobjects;

import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HourCountTest {

    private final static String COLLECTION = "collection";

    @Test
    void test_HourCount(){
        Member id1 = new Member("id1", COLLECTION, null, LocalDateTime.of(2023, 1, 5, 1, 5));
        Member id2 = new Member("id1", COLLECTION, null, LocalDateTime.of(2023, 1, 5, 1, 5));
        Member id3 = new Member("id1", COLLECTION, null, LocalDateTime.of(2023, 1, 5, 1, 15));
        Member id4 = new Member("id1", COLLECTION, null, LocalDateTime.of(2023, 1, 5, 2, 5));
        Member id5 = new Member("id1", COLLECTION, null, LocalDateTime.of(2023, 1, 5, 2, 25));
        Member id6 = new Member("id1", COLLECTION, null, LocalDateTime.of(2023, 1, 6, 1, 5));
        Member id7 = new Member("id1", COLLECTION, null, LocalDateTime.of(2023, 1, 6, 1, 2));
        Member id8 = new Member("id1", COLLECTION, null, LocalDateTime.of(2023, 1, 6, 2, 5));
        HourCount hourCount = new HourCount(List.of(id1, id2, id3, id4, id5, id6, id7, id8));

        Map<LocalDateTime, Integer> memberCountByHour = hourCount.getMemberCountByHour();

        assertEquals(4, hourCount.getMemberCountByHour().size());
        assertEquals(3,memberCountByHour.get(LocalDateTime.of(2023,1,5,1,0)));
        assertEquals(2,memberCountByHour.get(LocalDateTime.of(2023,1,5,2,0)));
        assertEquals(2,memberCountByHour.get(LocalDateTime.of(2023,1,6,1,0)));
        assertEquals(1,memberCountByHour.get(LocalDateTime.of(2023,1,6,2,0)));

    }

}