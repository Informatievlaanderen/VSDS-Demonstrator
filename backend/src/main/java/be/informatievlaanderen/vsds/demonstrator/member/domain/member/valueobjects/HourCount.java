package be.informatievlaanderen.vsds.demonstrator.member.domain.member.valueobjects;

import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HourCount {
    private final List<Member> memberList;

    public HourCount(List<Member> memberList) {
        this.memberList = memberList;
    }

    public Map<LocalDateTime, Integer> getMemberCountByHour() {
        return memberList
                .stream()
                .map(Member::getTimestamp)
                .map(localDateTime -> localDateTime.truncatedTo(ChronoUnit.HOURS))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(e -> 1)));
    }
}
