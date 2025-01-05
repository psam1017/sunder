package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.entity.AcademyShare;
import psam.portfolio.sunder.english.domain.academy.model.entity.QAcademyShare;
import psam.portfolio.sunder.english.domain.academy.model.response.AcademyShareSummary;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyShareQueryRepository;
import psam.portfolio.sunder.english.domain.academy.service.AcademyShareCommandService;
import psam.portfolio.sunder.english.domain.academy.service.AcademyShareQueryService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class AcademyShareQueryServiceTest extends AbstractSunderApplicationTest {

    @Autowired
    AcademyShareQueryService sut; // system under test

    @Autowired
    AcademyShareQueryRepository academyShareQueryRepository;

    @DisplayName("학원의 공유된 학원 목록을 조회할 수 있다.")
    @Test
    void getShares() {
        // given
        Academy sharingAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Academy sharedAcademy1 = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Academy sharedAcademy2 = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        dataCreator.registerAcademyShare(sharingAcademy, sharedAcademy1);
        dataCreator.registerAcademyShare(sharingAcademy, sharedAcademy2);

        // when
        List<AcademyShareSummary> shares = refreshAnd(() -> sut.getShares(sharingAcademy.getId()));

        // then
        assertThat(shares).hasSize(2);
        assertThat(shares)
                .extracting(as -> tuple(as.getAcademyId(), as.getName()))
                .containsExactlyInAnyOrder(
                        tuple(sharedAcademy1.getId(), sharedAcademy1.getName()),
                        tuple(sharedAcademy2.getId(), sharedAcademy2.getName())
                );
    }
}
