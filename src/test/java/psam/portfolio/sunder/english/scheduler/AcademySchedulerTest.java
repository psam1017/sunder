package psam.portfolio.sunder.english.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.SunderApplicationTests;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.domain.academy.scheduler.AcademyScheduler;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AcademySchedulerTest extends SunderApplicationTests {

    @Autowired
    AcademyScheduler sut;

    @Autowired
    AcademyQueryRepository academyQueryRepository;

    // 폐쇄 요청한지 7일 이상 지난 학원은 삭제
    @DisplayName("폐쇄 요청한지 7일 이상 지난 학원은 삭제할 수 있다.")
    @Test
    public void deleteWithdrawnAcademy() {
        // given
        Academy withdrawnTarget1 = registerAcademy(AcademyStatus.WITHDRAWN);
        withdrawnTarget1.setWithdrawalAt(LocalDateTime.now().minusDays(1));
        Academy withdrawnTarget2 = registerAcademy(AcademyStatus.WITHDRAWN);
        withdrawnTarget2.setWithdrawalAt(LocalDateTime.now().minusDays(1));
        Academy withdrawnTomorrow = registerAcademy(AcademyStatus.WITHDRAWN);
        withdrawnTomorrow.setWithdrawalAt(LocalDateTime.now().plusDays(1));

        // when
        refreshAnd(() -> sut.deleteWithdrawnAcademy());

        // then
        List<Academy> leftAcademies = academyQueryRepository.findAll();
        assertThat(leftAcademies).hasSize(1);
    }
}