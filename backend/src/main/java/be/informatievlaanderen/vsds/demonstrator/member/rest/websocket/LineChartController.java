package be.informatievlaanderen.vsds.demonstrator.member.rest.websocket;

import be.informatievlaanderen.vsds.demonstrator.member.application.services.MemberService;
import be.informatievlaanderen.vsds.demonstrator.member.rest.dtos.DataSetDto;
import be.informatievlaanderen.vsds.demonstrator.member.rest.dtos.LineChartDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;

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
                memberService.getLineChartDto();
        List<String> labels = List.of("Januari", "Februari", "Maart", "April", "Mei", "Juni");
        DataSetDto gipod = new DataSetDto("GIPOD", List.of((int) (Math.random() * 100), (int) (Math.random() * 100), (int) (Math.random() * 100), (int) (Math.random() * 100), (int) (Math.random() * 100), (int) (Math.random() * 100)));
        DataSetDto verkeer = new DataSetDto("VERKEER", List.of((int) (Math.random() * 100), (int) (Math.random() * 100), (int) (Math.random() * 100), (int) (Math.random() * 100), (int) (Math.random() * 100), (int) (Math.random() * 100)));

        List<DataSetDto> dataSetDtos = List.of(gipod, verkeer);

        this.template.convertAndSend("/broker/linechart", new LineChartDto(labels, dataSetDtos));
    }
}
