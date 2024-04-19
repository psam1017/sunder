package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.model.request.LostLoginPwForm;
import psam.portfolio.sunder.english.domain.user.service.UserCommandService;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_DIRECTOR;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_TEACHER;

class UserCommandServiceTest extends AbstractSunderApplicationTest {

    @Autowired
    UserCommandService sut; // system under test

    @Autowired
    TeacherQueryRepository teacherQueryRepository;

    @Autowired
    PasswordUtils passwordUtils;

    @DisplayName("비밀번호를 바꾸지 않더라도 비밀번호 변경 알림을 3개월 지연할 수 있다.")
    @Test
    void alterPasswordChangeLater() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
        director.setLastPasswordChangeDateTime(LocalDateTime.now().minusMonths(4));

        // when
        boolean result = refreshAnd(() -> sut.alterPasswordChangeLater(director.getId()));

        // then
        Teacher getDirector = teacherQueryRepository.getById(director.getId());
        assertThat(result).isTrue();
        assertThat(getDirector.isPasswordExpired()).isFalse();
    }

    @DisplayName("사용자의 아이디, 이메일, 이름으로 새로운 비밀번호를 생성하고 이메일로 전송할 수 있다.")
    @Test
    void issueTempPassword() {
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        LostLoginPwForm userInfo = new LostLoginPwForm(director.getLoginId(), director.getEmail(), director.getName());

        // when
        boolean result = refreshAnd(() -> sut.issueNewPassword(userInfo));

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("비밀번호를 변경하기 위해 기존 비밀번호를 입력하고 비밀번호 변경을 인가하는 토큰을 받을 수 있다.")
    @Test
    void requestPasswordChange() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        // when
        Integer passwordChangeAllowedAmount = refreshAnd(() -> sut.authenticateToChangePassword(director.getId(), infoContainer.getAnyRawPassword()));

        // then
        // 비밀번호 변경이 유효한 시간은 3분이다.
        assertThat(passwordChangeAllowedAmount).isEqualTo(3);
    }

    @DisplayName("비밀번호 변경을 인가하는 토큰으로 비밀번호를 변경할 수 있다.")
    @Test
    void changePassword() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        sut.authenticateToChangePassword(director.getId(), infoContainer.getAnyRawPassword());
        String newPassword = "asd456$%^";

        // when
        boolean result = refreshAnd(() -> sut.changePassword(director.getId(), newPassword));

        // then
        Teacher getDirector = teacherQueryRepository.getById(director.getId());
        assertThat(result).isTrue();
        assertThat(passwordUtils.matches(newPassword, getDirector.getLoginPw())).isTrue();
    }
}
