package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.SunderApplicationTests;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.exception.LoginFailException;
import psam.portfolio.sunder.english.domain.user.exception.OneParamToCheckUserDuplException;
import psam.portfolio.sunder.english.domain.user.model.request.UserLoginForm;
import psam.portfolio.sunder.english.domain.user.model.request.UserPOSTLostPw;
import psam.portfolio.sunder.english.domain.user.model.response.LoginResult;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.domain.user.service.UserCommandService;
import psam.portfolio.sunder.english.domain.user.service.UserQueryService;
import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_DIRECTOR;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_TEACHER;

class UserCommandServiceTest extends SunderApplicationTests {

    @Autowired
    UserCommandService sut; // system under test

    @Autowired
    UserQueryService userQueryService;

    @Autowired
    TeacherQueryRepository teacherQueryRepository;

    @Autowired
    PasswordUtils passwordUtils;

    @DisplayName("비밀번호를 바꾸지 않더라도 비밀번호 변경 알림을 3개월 지연할 수 있다.")
    @Test
    void delayPasswordChange() {
        // given
        Academy academy = registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = registerTeacher(UserStatus.ACTIVE, academy);
        createRole(director, ROLE_DIRECTOR, ROLE_TEACHER);
        director.setLastPasswordChangeDateTime(LocalDateTime.now().minusMonths(4));

        // when
        boolean result = refreshAnd(() -> sut.delayPasswordChange(director.getUuid()));

        // then
        Teacher getDirector = teacherQueryRepository.getById(director.getUuid());
        assertThat(result).isTrue();
        assertThat(getDirector.isPasswordExpired()).isFalse();
    }

    @DisplayName("사용자의 아이디, 이메일, 이름으로 임시 비밀번호를 생성하고 이메일로 전송할 수 있다.")
    @Test
    void issueTempPassword() {
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        Academy academy = registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = registerTeacher(UserStatus.ACTIVE, academy);
        createRole(director, ROLE_DIRECTOR, ROLE_TEACHER);

        UserPOSTLostPw userInfo = new UserPOSTLostPw(director.getLoginId(), director.getEmail(), director.getName());

        // when
        boolean result = refreshAnd(() -> sut.issueTempPassword(userInfo));

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("비밀번호 변경을 인가하는 토큰으로 비밀번호를 변경할 수 있다.")
    @Test
    void changePassword() {
        // given
        Academy academy = registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = registerTeacher(UserStatus.ACTIVE, academy);
        createRole(director, ROLE_DIRECTOR, ROLE_TEACHER);

        String token = userQueryService.requestPasswordChange(director.getUuid(), "qwe123!@#");
        String newPassword = "asd456$%^";

        // when
        boolean result = refreshAnd(() -> sut.changePassword(token, newPassword));

        // then
        Teacher getDirector = teacherQueryRepository.getById(director.getUuid());
        assertThat(result).isTrue();
        assertThat(passwordUtils.matches(newPassword, getDirector.getLoginPw())).isTrue();
    }
}
