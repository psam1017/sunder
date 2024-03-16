package psam.portfolio.sunder.english.domain.academy.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.entity.QAcademy;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyCommandRepository;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyQueryRepository;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Component
public class AcademyScheduler {

    private final AcademyQueryRepository academyQueryRepository;
    private final AcademyCommandRepository academyCommandRepository;

    /**
     * 매일 0시 1분에 탈퇴 요청 후 7일 이상 지난 학원은 삭제
     */
    @Scheduled(cron = "0 1 0 * * *")
    public void deleteWithdrawnAcademy() {
        List<Academy> deleteAcademies = academyQueryRepository.findAll(
                QAcademy.academy.status.eq(AcademyStatus.WITHDRAWN),
                QAcademy.academy.withdrawalAt.before(LocalDateTime.now())
        );
        academyCommandRepository.deleteAll(deleteAcademies);

//        academyCommandRepository.deleteAllByStatusAndWithdrawalAtBefore(
//                AcademyStatus.WITHDRAWN,
//                LocalDateTime.now()
//        );
    }
}
