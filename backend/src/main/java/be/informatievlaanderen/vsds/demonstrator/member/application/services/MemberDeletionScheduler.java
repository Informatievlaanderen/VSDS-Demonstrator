package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import be.informatievlaanderen.vsds.demonstrator.member.domain.member.repositories.MemberRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MemberDeletionScheduler {
	private final MemberRepository memberRepository;

	public MemberDeletionScheduler(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Scheduled(cron = "0 0 0 * * *")
	public void deleteMembers() {
		memberRepository.deleteMembersOlderThenSevenDays();
	}
}
