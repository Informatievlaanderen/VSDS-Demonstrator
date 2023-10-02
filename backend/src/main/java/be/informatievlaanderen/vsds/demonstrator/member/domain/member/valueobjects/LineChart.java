package be.informatievlaanderen.vsds.demonstrator.member.domain.member.valueobjects;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LineChart {
    private final Map<LocalDateTime, Integer> memberCountByHour;
    private final long numberOfMembersOutsideFrame;
    private final List<String> labels;
    private final List<Integer> values;
    private final LocalDateTime startDate;

    public LineChart(LocalDateTime startDate, long numberOfMembersOutsideFrame, Map<LocalDateTime, Integer> memberCountByHour) {
        this.startDate = startDate;
        this.numberOfMembersOutsideFrame = numberOfMembersOutsideFrame;
        this.memberCountByHour = memberCountByHour;
        this.labels = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    private void calculatePointElements() {
        LocalDateTime startTime = startDate.truncatedTo(ChronoUnit.HOURS);
        long memberCount = numberOfMembersOutsideFrame;
        while (startTime.isBefore(LocalDateTime.now())) {
            memberCount = memberCount + memberCountByHour.getOrDefault(startTime, 0);
            if (memberCount > 0) {
                labels.add(startTime.toString());
                values.add((int) memberCount);
            }
            startTime = startTime.plusHours(1);
        }
    }

    public List<String> getLabels() {
        if (labels.isEmpty())
            calculatePointElements();
        return labels;
    }

    //TODO multiple streams
    public List<List<Integer>> getValues() {
        if (values.isEmpty())
            calculatePointElements();
        return List.of(values);
    }
}
