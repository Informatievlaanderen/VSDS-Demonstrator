package be.informatievlaanderen.vsds.demonstrator.member.rest.websocket;

import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.MemberDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {
	private SimpMessagingTemplate template;
	private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

	@Autowired
	public MessageController(SimpMessagingTemplate template) {
		this.template = template;
	}

	public void send(MemberDto member) {
		log.info("sending member");
		this.template.convertAndSend("/broker/member", member);
	}
}