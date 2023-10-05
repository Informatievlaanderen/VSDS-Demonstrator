package be.informatievlaanderen.vsds.demonstrator.member.domain.member.valueobjects;

public class Dataset {
    private final String collection;
    private final long membersOutsideTimeFrame;
    private final HourCount hourCount;
    public Dataset(String collection, long membersOutsideTimeFrame, HourCount hourCount) {
        this.collection=collection;
        this.membersOutsideTimeFrame = membersOutsideTimeFrame;
        this.hourCount=hourCount;
    }

    public String getCollection() {
        return collection;
    }

    public long getMembersOutsideTimeFrame() {
        return membersOutsideTimeFrame;
    }

    public HourCount getHourCount() {
        return hourCount;
    }
}
