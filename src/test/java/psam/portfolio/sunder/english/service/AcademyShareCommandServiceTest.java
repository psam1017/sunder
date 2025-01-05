package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.exception.DuplicateAcademyException;
import psam.portfolio.sunder.english.domain.academy.exception.IllegalStatusAcademyException;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.entity.AcademyShare;
import psam.portfolio.sunder.english.domain.academy.model.entity.QAcademyShare;
import psam.portfolio.sunder.english.domain.academy.model.request.AcademyDirectorPOST.AcademyPOST;
import psam.portfolio.sunder.english.domain.academy.model.request.AcademyDirectorPOST.DirectorPOST;
import psam.portfolio.sunder.english.domain.academy.model.request.AcademyPATCH;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyShareQueryRepository;
import psam.portfolio.sunder.english.domain.academy.service.AcademyCommandService;
import psam.portfolio.sunder.english.domain.academy.service.AcademyShareCommandService;
import psam.portfolio.sunder.english.domain.teacher.exception.RoleDirectorRequiredException;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.exception.DuplicateUserException;
import psam.portfolio.sunder.english.domain.user.exception.IllegalStatusUserException;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;
import psam.portfolio.sunder.english.domain.user.model.request.UserLoginForm;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_DIRECTOR;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_TEACHER;

public class AcademyShareCommandServiceTest extends AbstractSunderApplicationTest {

    @Autowired
    AcademyShareCommandService sut; // system under test

    @Autowired
    AcademyShareQueryRepository academyShareQueryRepository;

    @DisplayName("학원이 다른 학원을 공유 대상으로 삼을 수 있다.")
    @Test
    void share() {
        // given
        Academy sharingAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Academy sharedAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);

        // when
        refreshAnd(() -> sut.share(sharingAcademy.getId(), sharedAcademy.getId()));

        // then
        Optional<AcademyShare> academyShare = academyShareQueryRepository.findOne(
                QAcademyShare.academyShare.sharingAcademy.id.eq(sharingAcademy.getId()),
                QAcademyShare.academyShare.sharedAcademy.id.eq(sharedAcademy.getId())
        );
        assertThat(academyShare).isPresent();
    }

    @DisplayName("공유된 학원을 취소할 수 있다.")
    @Test
    void cancelShare() {
        // given
        Academy sharingAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Academy sharedAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        dataCreator.registerAcademyShare(sharingAcademy, sharedAcademy);

        // when
        refreshAnd(() -> sut.cancelShare(sharingAcademy.getId(), sharedAcademy.getId()));

        // then
        Optional<AcademyShare> academyShare = academyShareQueryRepository.findOne(
                QAcademyShare.academyShare.sharingAcademy.id.eq(sharingAcademy.getId()),
                QAcademyShare.academyShare.sharedAcademy.id.eq(sharedAcademy.getId())
        );
        assertThat(academyShare).isEmpty();
    }
}
