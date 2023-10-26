package be.informatievlaanderen.vsds.demonstrator.member.presentation.dtos;

import java.util.List;

public class DataSetDto {
    private final String name;
    private final List<Integer> values;

    public DataSetDto(String name, List<Integer> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getValues() {
        return values;
    }
}
