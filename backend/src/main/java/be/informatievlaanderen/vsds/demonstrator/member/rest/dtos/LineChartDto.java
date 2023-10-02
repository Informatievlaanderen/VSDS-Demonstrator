package be.informatievlaanderen.vsds.demonstrator.member.rest.dtos;

import java.util.List;

public class LineChartDto {
    private final List<String> labels;
    private final List<List<Integer>> values;

    public LineChartDto(List<String> labels, List<List<Integer>> values) {
        this.labels = labels;
        this.values = values;
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<List<Integer>> getValues() {
        return values;
    }
}
