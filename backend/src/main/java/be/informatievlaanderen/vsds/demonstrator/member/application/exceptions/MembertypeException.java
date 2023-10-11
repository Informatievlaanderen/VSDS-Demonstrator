package be.informatievlaanderen.vsds.demonstrator.member.application.exceptions;

public class MembertypeException extends RuntimeException{
    private final String membertype;
    private final String member;

    public MembertypeException(String membertype, String member) {
        this.membertype = membertype;
        this.member = member;
    }

    @Override
    public String getMessage() {
        return "Member " + member + " was not of expected type " + membertype;
    }
}
