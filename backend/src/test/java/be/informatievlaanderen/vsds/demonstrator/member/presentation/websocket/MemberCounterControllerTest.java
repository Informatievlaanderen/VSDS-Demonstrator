package be.informatievlaanderen.vsds.demonstrator.member.presentation.websocket;

import be.informatievlaanderen.vsds.demonstrator.member.application.services.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberCounterControllerTest {

    @Mock
    private MemberService memberService;
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;
    @InjectMocks
    private MemberCounterController memberCounterController;

    @Test
    void test_numberOfMembersIsTransferredToWebsocket() {
        when(memberService.getNumberOfMembers()).thenReturn(76L);
        memberCounterController.send();

        verify(memberService).getNumberOfMembers();
        verify(simpMessagingTemplate).convertAndSend("/broker/membercounter", 76L);
    }

}