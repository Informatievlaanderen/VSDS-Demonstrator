package be.informatievlaanderen.vsds.demonstrator.member.presentation.websocket;

import be.informatievlaanderen.vsds.demonstrator.member.application.services.MemberService;
import be.informatievlaanderen.vsds.demonstrator.member.presentation.dtos.LineChartDto;
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
    void test_lineChartIsTransferredToWebsocket() {
        LineChartDto lineChartDto = new LineChartDto(List.of(), List.of());
        when(memberService.getLineChartDtos()).thenReturn(lineChartDto);
        lineChartController.send();

        verify(memberService).getLineChartDtos();
        verify(simpMessagingTemplate).convertAndSend("/broker/linechart", lineChartDto);
    }

}