package be.informatievlaanderen.vsds.demonstrator.member.domain.member.valueobjects;

import be.informatievlaanderen.vsds.demonstrator.member.rest.dtos.DataSetDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class LineChart {
    private final List<String> labels;
    private final List<DataSetDto> dataSetDtos;
    private final LocalDateTime startDate;

    public LineChart(LocalDateTime startDate) {
        this.startDate = startDate;
        this.labels = new ArrayList<>();
        this.dataSetDtos = new ArrayList<>();
        initializeLabels();
    }

    private void initializeLabels() {
        LocalDateTime startTime = startDate.truncatedTo(ChronoUnit.HOURS);
        while (startTime.isBefore(LocalDateTime.now())) {
            labels.add(startTime.toString());
            startTime = startTime.plusHours(1);
        }
    }

    public void addDataSet(Dataset dataset) {

        LocalDateTime startTime = startDate.truncatedTo(ChronoUnit.HOURS);
        long memberCount = dataset.getMembersOutsideTimeFrame();
        List<Integer> values = new ArrayList<>();
        while (startTime.isBefore(LocalDateTime.now())) {
            memberCount = memberCount + dataset.getHourCount().getMemberCountByHour().getOrDefault(startTime, 0);
            values.add((int) memberCount);
            startTime = startTime.plusHours(1);
        }
        dataSetDtos.add(new DataSetDto(dataset.getCollection(), values));
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<DataSetDto> getDatasetDtos() {
        return dataSetDtos;
    }
}
