package be.informatievlaanderen.vsds.demonstrator.member.presentation.websocket;

import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.MemberIngestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {
    private final SimpMessagingTemplate template;
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    public MessageController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @EventListener(MemberIngestedEvent.class)
    public void send(MemberIngestedEvent memberIngestedEvent) {
        log.info("sending member");
        this.template.convertAndSend("/broker/member/", memberIngestedEvent.getMemberDto());
    }
}
