package be.informatievlaanderen.vsds.demonstrator.member.rest.websocket;

import be.informatievlaanderen.vsds.demonstrator.member.application.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class LineChartController {
    private final SimpMessagingTemplate template;
    private final MemberService memberService;

    @Autowired
    public LineChartController(SimpMessagingTemplate template, MemberService memberService) {
        this.template = template;
        this.memberService = memberService;
    }

    @Scheduled(fixedDelay = 1000)
    public void send() {
        this.template.convertAndSend("/broker/linechart", memberService.getLineChartDtos());
    }
}
