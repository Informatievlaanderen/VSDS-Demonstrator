package be.informatievlaanderen.vsds.demonstrator.member.application.exceptions;

public class MemberTypeException extends RuntimeException {
    private final String collection;
    private final String memberType;

    public MemberTypeException(String collection, String memberType) {
        this.collection = collection;
        this.memberType = memberType;

    }

    @Override
    public String getMessage() {
        return "Member of collection %s was not of expected type %s".formatted(collection, memberType);
    }
}
