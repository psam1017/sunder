package psam.portfolio.sunder.english.domain.academy.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyCommandRepository;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional
@Component
public class AcademyScheduler {

    private final AcademyCommandRepository academyCommandRepository;

    // TODO 학원 소속의 모든 엔티티들 모두 삭제
    /**
     * 매일 0시 1분에 탈퇴 요청 후 7일 이상 지난 학원은 삭제
     */
    @Scheduled(cron = "0 1 0 * * *")
    public void deleteWithdrawnAcademy() {
        academyCommandRepository.deleteAllByStatusAndWithdrawalAtBefore(
                AcademyStatus.WITHDRAWN,
                LocalDateTime.now()
        );
    }
}
