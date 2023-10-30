package be.informatievlaanderen.vsds.demonstrator.member.presentation.websocket;

import be.informatievlaanderen.vsds.demonstrator.member.application.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class MemberCounterController {
    private final SimpMessagingTemplate template;
    private final MemberService memberService;

    @Autowired
    public MemberCounterController(SimpMessagingTemplate template, MemberService memberService) {
        this.template = template;
        this.memberService = memberService;
    }

    @Scheduled(fixedDelay = 1000)
    public void send() {
        long counter = memberService.getNumberOfMembers();
        this.template.convertAndSend("/broker/membercounter", counter);
    }
}
