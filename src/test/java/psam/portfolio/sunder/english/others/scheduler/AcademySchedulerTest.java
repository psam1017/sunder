package psam.portfolio.sunder.english.others.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.domain.academy.scheduler.AcademyScheduler;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AcademySchedulerTest extends AbstractSunderApplicationTest {

    @Autowired
    AcademyScheduler sut;

    @Autowired
    AcademyQueryRepository academyQueryRepository;

    @DisplayName("폐쇄 요청한지 7일 이상 지난 학원은 삭제할 수 있다.")
    @Test
    public void deleteWithdrawnAcademy() {
        // given
        Academy withdrawnTarget1 = dataCreator.registerAcademy(AcademyStatus.WITHDRAWN);
        withdrawnTarget1.setWithdrawalAt(LocalDateTime.now().minusDays(1));
        Academy withdrawnTarget2 = dataCreator.registerAcademy(AcademyStatus.WITHDRAWN);
        withdrawnTarget2.setWithdrawalAt(LocalDateTime.now().minusDays(1));
        Academy withdrawnTomorrow = dataCreator.registerAcademy(AcademyStatus.WITHDRAWN);
        withdrawnTomorrow.setWithdrawalAt(LocalDateTime.now().plusDays(1));

        // when
        refreshAnd(() -> sut.deleteWithdrawnAcademy());

        // then
        List<Academy> leftAcademies = academyQueryRepository.findAll();
        assertThat(leftAcademies).hasSize(1);
    }
}