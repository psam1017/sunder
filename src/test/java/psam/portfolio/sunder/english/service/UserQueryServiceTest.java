package psam.portfolio.sunder.english.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.student.model.response.StudentFullResponse;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.model.response.TeacherFullResponse;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.exception.LoginFailException;
import psam.portfolio.sunder.english.domain.user.exception.OneParamToCheckUserDuplException;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;
import psam.portfolio.sunder.english.domain.user.model.request.UserLoginForm;
import psam.portfolio.sunder.english.domain.user.model.request.LostLoginIdForm;
import psam.portfolio.sunder.english.domain.user.model.response.LoginResult;
import psam.portfolio.sunder.english.domain.user.model.response.TokenRefreshResponse;
import psam.portfolio.sunder.english.domain.user.service.UserQueryService;
import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtClaim;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.*;
import static psam.portfolio.sunder.english.infrastructure.jwt.JwtClaim.ROLE_NAMES;

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
        assertThatThrownBy(() -> refreshAnd(() -> sut.checkDuplication(loginId, email, phone, null)))
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
        assertThatThrownBy(() -> refreshAnd(() -> sut.checkDuplication(loginId, email, phone, null)))
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
        boolean isOk = refreshAnd(() -> sut.checkDuplication(loginId, email, phone, null));

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
        boolean isOk = refreshAnd(() -> sut.checkDuplication(loginId, email, phone, null));

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
        boolean isOk = refreshAnd(() -> sut.checkDuplication(loginId, email, phone, null));

        // then
        assertThat(isOk).isFalse();
    }

    @DisplayName("사용자 자기 자신은 중복 검사에서 제외된다.")
    @Test
    void ifUserIdExistOk(){
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);

        String loginId = teacher.getLoginId();
        String email = null;
        String phone = null;

        // when
        boolean isOk = refreshAnd(() -> sut.checkDuplication(loginId, email, phone, teacher.getId()));

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

        UserLoginForm loginForm = new UserLoginForm(director.getLoginId(), infoContainer.getAnyRawPassword());
        String remoteIp = "127.0.0.1";

        // when
        LoginResult result = refreshAnd(() -> sut.login(loginForm, remoteIp));

        // then
        String accessToken = result.getAccessToken();
        String accessTokenSubject = jwtUtils.extractSubject(accessToken);
        String refreshTokenSubject = jwtUtils.extractSubject(result.getRefreshToken());
        assertThat(accessTokenSubject).isEqualTo(director.getId().toString());
        assertThat(refreshTokenSubject).isEqualTo(director.getId().toString());
        Claims claims = jwtUtils.extractAllClaims(accessToken);
        assertThat(claims.get(JwtClaim.USER_STATUS.toString(), String.class)).isEqualTo(UserStatus.ACTIVE.toString());
        assertThat(claims.get(JwtClaim.REMOTE_IP.toString(), String.class)).isEqualTo(remoteIp);

        assertThat(result.getUserId()).isEqualTo(director.getId().toString());
        assertThat(result.getAcademyId()).isEqualTo(director.getAcademy().getId().toString());
        assertThat(result.getRoles()).containsExactlyInAnyOrderElementsOf(director.getRoles().stream().map(UserRole::getRoleName).toList());
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @DisplayName("사용자 로그인 아이디가 틀리면 로그인할 수 없다.")
    @Test
    void loginFailByLoginId() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        UserLoginForm loginForm = new UserLoginForm(director.getLoginId().substring(3), infoContainer.getAnyRawPassword());
        String remoteIp = "127.0.0.1";

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.login(loginForm, remoteIp)))
                .isInstanceOf(LoginFailException.class);
    }

    @DisplayName("사용자 로그인 비밀번호가 틀리면 로그인할 수 없다.")
    @Test
    void loginFailByLoginPw() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        UserLoginForm loginForm = new UserLoginForm(director.getLoginId(), infoContainer.getAnyRawPassword().substring(3));
        String remoteIp = "127.0.0.1";

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.login(loginForm, remoteIp)))
                .isInstanceOf(LoginFailException.class);
    }

    @DisplayName("서비스를 사용할 수 없는 사용자는 로그인할 수 없다.")
    @Test
    void loginFailByStatus() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.FORBIDDEN, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        UserLoginForm loginForm = new UserLoginForm(director.getLoginId(), infoContainer.getAnyRawPassword());
        String remoteIp = "127.0.0.1";

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.login(loginForm, remoteIp)))
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

        UserLoginForm loginForm = new UserLoginForm(director.getLoginId(), infoContainer.getAnyRawPassword());
        String remoteIp = "127.0.0.1";

        // when
        LoginResult result = refreshAnd(() -> sut.login(loginForm, remoteIp));

        // then
        assertThat(result.isPasswordChangeRequired()).isTrue();
    }

    @DisplayName("사용자에게 새로운 토큰을 발급할 수 있다.")
    @Test
    void refreshToken() throws JsonProcessingException {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
        String remoteIp = "127.0.0.1";

        // when
        TokenRefreshResponse response = refreshAnd(() -> sut.refreshToken(director.getId(), remoteIp));

        // then
        String accessToken = response.getAccessToken();
        String accessTokenSubject = jwtUtils.extractSubject(accessToken);
        assertThat(accessTokenSubject).isEqualTo(director.getId().toString());

        Claims claims = jwtUtils.extractAllClaims(accessToken);
        assertThat(claims.get(JwtClaim.USER_STATUS.toString(), String.class)).isEqualTo(UserStatus.ACTIVE.toString());
        assertThat(claims.get(JwtClaim.REMOTE_IP.toString(), String.class)).isEqualTo(remoteIp);

        List<RoleName> roleNames = objectMapper.readValue(claims.get(ROLE_NAMES.toString(), String.class), new TypeReference<>() {});
        assertThat(roleNames).containsExactlyInAnyOrderElementsOf(director.getRoles().stream().map(UserRole::getRoleName).toList());
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

    @DisplayName("선생님이 자기 자신의 정보를 조회할 수 있다.")
    @Test
    void getMyInfoByTeacher() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        // when
        Object myInfo = refreshAnd(() -> sut.getMyInfo(director.getId()));

        // then
        assertThat(myInfo instanceof TeacherFullResponse).isTrue();
    }

    @DisplayName("학생이 자기 자신의 정보를 조회할 수 있다.")
    @Test
    void getMyInfoByStudent() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, ROLE_STUDENT);

        // when
        Object myInfo = refreshAnd(() -> sut.getMyInfo(student.getId()));

        // then
        assertThat(myInfo instanceof StudentFullResponse).isTrue();
        if (myInfo instanceof StudentFullResponse s) {
            assertThat(StringUtils.hasText(s.getNote())).isFalse();
        }
    }
}
