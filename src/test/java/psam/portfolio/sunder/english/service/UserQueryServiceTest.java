package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.exception.LoginFailException;
import psam.portfolio.sunder.english.domain.user.exception.OneParamToCheckUserDuplException;
import psam.portfolio.sunder.english.domain.user.model.request.UserLoginForm;
import psam.portfolio.sunder.english.domain.user.model.request.LostLoginIdForm;
import psam.portfolio.sunder.english.domain.user.model.response.LoginResult;
import psam.portfolio.sunder.english.domain.user.model.response.TokenRefreshResponse;
import psam.portfolio.sunder.english.domain.user.service.UserQueryService;
import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtClaim;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_DIRECTOR;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_TEACHER;

@SuppressWarnings("ConstantValue")
class UserQueryServiceTest extends AbstractSunderApplicationTest {

    @Autowired
    UserQueryService sut; // system under test

    @Autowired
    TeacherCommandRepository teacherCommandRepository;

    @Autowired
    JwtUtils jwtUtils;

    @DisplayName("중복 검사를 위해서 loginId, email, phone 중 하나만 입력해야 한다.")
    @Test
    void oneParamToCheckDuplException(){
        // given
        String loginId = "loginId";
        String email = "email";
        String phone = "";

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.checkDuplication(loginId, email, phone)))
                .isInstanceOf(OneParamToCheckUserDuplException.class);
    }

    @DisplayName("중복 검사를 위해서 loginId, email, phone 중 하나는 반드시 입력해야 한다.")
    @Test
    void noParamToCheckDuplException(){
        // given
        String loginId = "";
        String email = "";
        String phone = "";

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.checkDuplication(loginId, email, phone)))
                .isInstanceOf(OneParamToCheckUserDuplException.class);
    }

    @DisplayName("로그인 아이디의 중복 검사를 수행할 수 있다.")
    @Test
    void checkLoginIdDupl(){
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);

        String loginId = teacher.getLoginId();
        String email = null;
        String phone = null;

        // when
        boolean isOk = refreshAnd(() -> sut.checkDuplication(loginId, email, phone));

        // then
        assertThat(isOk).isFalse();
    }

    @DisplayName("이메일의 중복 검사를 수행할 수 있다.")
    @Test
    void checkEmailDupl(){
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);

        String loginId = null;
        String email = teacher.getEmail();
        String phone = null;

        // when
        boolean isOk = refreshAnd(() -> sut.checkDuplication(loginId, email, phone));

        // then
        assertThat(isOk).isFalse();
    }

    @DisplayName("연락처의 중복 검사를 수행할 수 있다.")
    @Test
    void checkPhoneDupl(){
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);

        String loginId = null;
        String email = null;
        String phone = teacher.getPhone();

        // when
        boolean isOk = refreshAnd(() -> sut.checkDuplication(loginId, email, phone));

        // then
        assertThat(isOk).isFalse();
    }

    @DisplayName("PENDING 상태의 사용자는 중복 검사에서 제외된다.")
    @Test
    void ifPendingOk(){
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.PENDING, academy);

        String loginId = teacher.getLoginId();
        String email = null;
        String phone = null;

        // when
        boolean isOk = refreshAnd(() -> sut.checkDuplication(loginId, email, phone));

        // then
        assertThat(isOk).isTrue();
    }

    @DisplayName("이메일 인증을 하지 않은 사용자는 중복 검사에서 제외된다.")
    @Test
    void ifEmailNotVerifiedOk(){
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        teacher.verifyEmail(false);

        String loginId = teacher.getLoginId();
        String email = null;
        String phone = null;

        // when
        boolean isOk = refreshAnd(() -> sut.checkDuplication(loginId, email, phone));

        // then
        assertThat(isOk).isTrue();
    }

    @DisplayName("사용자가 로그인하고 토큰을 발급받을 수 있다.")
    @Test
    void loginAndGetToken() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        UserLoginForm loginForm = new UserLoginForm(director.getLoginId(), infoContainer.getRawPassword());

        // when
        LoginResult result = refreshAnd(() -> sut.login(loginForm));

        // then
        String subject = jwtUtils.extractSubject(result.getToken());
        assertThat(result.getType()).isEqualTo("Bearer ");
        assertThat(subject).isEqualTo(director.getUuid().toString());
        assertThat(result.isPasswordChangeRequired()).isFalse();
    }

    @DisplayName("사용자 로그인 아이디가 틀리면 로그인할 수 없다.")
    @Test
    void loginFailByLoginId() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        UserLoginForm loginForm = new UserLoginForm(director.getLoginId().substring(3), infoContainer.getRawPassword());

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.login(loginForm)))
                .isInstanceOf(LoginFailException.class);
    }

    @DisplayName("사용자 로그인 비밀번호가 틀리면 로그인할 수 없다.")
    @Test
    void loginFailByLoginPw() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        UserLoginForm loginForm = new UserLoginForm(director.getLoginId(), infoContainer.getRawPassword().substring(3));

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.login(loginForm)))
                .isInstanceOf(LoginFailException.class);
    }

    @DisplayName("서비스를 사용할 수 없는 사용자는 로그인할 수 없다.")
    @Test
    void loginFailByStatus() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.FORBIDDEN, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        UserLoginForm loginForm = new UserLoginForm(director.getLoginId(), infoContainer.getRawPassword());

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.login(loginForm)))
                .isInstanceOf(ApiException.class);
    }

    @DisplayName("로그인 시점에 마지막으로 비밀번호를 변경한지 3개월이 지났는지 여부를 알 수 있다.")
    @Test
    void passwordChangeRequiredTrue() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
        director.setLastPasswordChangeDateTime(LocalDateTime.now().minusMonths(4));

        UserLoginForm loginForm = new UserLoginForm(director.getLoginId(), infoContainer.getRawPassword());

        // when
        LoginResult result = refreshAnd(() -> sut.login(loginForm));

        // then
        assertThat(result.isPasswordChangeRequired()).isTrue();
    }

    @DisplayName("사용자에게 새로운 토큰을 발급할 수 있다.")
    @Test
    void refreshToken() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        // when
        TokenRefreshResponse response = refreshAnd(() -> sut.refreshToken(director.getUuid()));

        // then
        String subject = jwtUtils.extractSubject(response.getToken());
        assertThat(subject).isEqualTo(director.getUuid().toString());
        assertThat(response.getType()).isEqualTo("Bearer ");
    }

    @DisplayName("사용자의 이메일과 이름으로 로그인 아이디를 이메일로 전송할 수 있다.")
    @Test
    void findLoginId() {
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        LostLoginIdForm userInfo = new LostLoginIdForm(director.getEmail(), director.getName());

        // when
        boolean result = refreshAnd(() -> sut.findLoginId(userInfo));

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
        TokenRefreshResponse response = refreshAnd(() -> sut.authenticateToChangePassword(director.getUuid(), infoContainer.getRawPassword()));

        // then
        Boolean changeable = jwtUtils.extractClaim(response.getToken(), claims -> claims.get(JwtClaim.PASSWORD_CHANGE.toString(), Boolean.class));
        assertThat(changeable).isTrue();
        assertThat(response.getType()).isEqualTo("Bearer ");
    }
}
