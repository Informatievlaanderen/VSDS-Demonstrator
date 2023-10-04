package be.informatievlaanderen.vsds.demonstrator.member.rest.websocket;

import be.informatievlaanderen.vsds.demonstrator.member.application.services.MemberService;
import be.informatievlaanderen.vsds.demonstrator.member.rest.dtos.LineChartDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineChartControllerTest {

    @Mock
    private MemberService memberService;
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;
    @InjectMocks
    private LineChartController lineChartController;

    @Test
    void test_lineChartIsTransferredToWebsocket(){
        LineChartDto lineChartDto = new LineChartDto(List.of(), List.of());
        when(memberService.getLineChartDto()).thenReturn(lineChartDto);
        lineChartController.send();

        verify(memberService).getLineChartDto();
        verify(simpMessagingTemplate).convertAndSend("/broker/linechart",lineChartDto);
    }

}