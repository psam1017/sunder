package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.SunderApplicationTests;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.exception.OneParamToCheckUserDuplException;
import psam.portfolio.sunder.english.domain.user.service.UserQueryService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("ConstantValue")
class UserQueryServiceTest extends SunderApplicationTests {

    @Autowired
    UserQueryService sut; // system under test

    @Autowired
    TeacherCommandRepository teacherCommandRepository;

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
        Academy academy = registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = registerTeacher(UserStatus.ACTIVE, academy);

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
        Academy academy = registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = registerTeacher(UserStatus.ACTIVE, academy);

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
        Academy academy = registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = registerTeacher(UserStatus.ACTIVE, academy);

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
        Academy academy = registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = registerTeacher(UserStatus.PENDING, academy);

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
        Academy academy = registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = registerTeacher(UserStatus.ACTIVE, academy);
        teacher.verifyEmail(false);

        String loginId = teacher.getLoginId();
        String email = null;
        String phone = null;

        // when
        boolean isOk = refreshAnd(() -> sut.checkDuplication(loginId, email, phone));

        // then
        assertThat(isOk).isTrue();
    }
}
