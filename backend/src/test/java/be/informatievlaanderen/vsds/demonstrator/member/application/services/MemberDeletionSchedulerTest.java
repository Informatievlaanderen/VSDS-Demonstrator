package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import be.informatievlaanderen.vsds.demonstrator.member.domain.member.repositories.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberDeletionSchedulerTest {
	@Mock
	private MemberRepository memberRepository;
	@InjectMocks
	private MemberDeletionScheduler memberDeletionScheduler;

	@Test
	void test_DeleteMembersOlderThenSevenDays() {
		memberDeletionScheduler.deleteMembers();

		verify(memberRepository).deleteMembersOlderThenSevenDays();
	}
}