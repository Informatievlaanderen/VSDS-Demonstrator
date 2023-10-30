package be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects;

import org.jetbrains.annotations.NotNull;

public class MemberIngestedEvent {
    private final MemberDto memberDto;

    public MemberIngestedEvent(@NotNull MemberDto memberDto) {
        this.memberDto = memberDto;
    }

    public MemberDto getMemberDto() {
        return memberDto;
    }
}