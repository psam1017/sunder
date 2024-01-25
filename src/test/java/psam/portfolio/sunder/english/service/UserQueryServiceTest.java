package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.SunderApplicationTests;
import psam.portfolio.sunder.english.web.teacher.model.Teacher;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.web.user.exception.NoParamToCheckDuplException;
import psam.portfolio.sunder.english.web.user.exception.OneParamToCheckDuplException;
import psam.portfolio.sunder.english.web.user.service.UserQueryService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("ConstantValue")
class UserQueryServiceTest extends SunderApplicationTests {

    @Autowired
    UserQueryService userQueryService;

    @Autowired
    TeacherCommandRepository teacherCommandRepository;

    @DisplayName("중복 검사를 위해서 loginId, email, phone 중 하나만 입력해야 한다.")
    @Test
    void oneParamToCheckDuplException(){
        // given
        String loginId = "loginId";
        String email = "email";
        String phone = "phone";

        // when
        // then
        assertThatThrownBy(() -> userQueryService.checkDuplication(loginId, email, phone))
                .isInstanceOf(OneParamToCheckDuplException.class);
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
        assertThatThrownBy(() -> userQueryService.checkDuplication(loginId, email, phone))
                .isInstanceOf(NoParamToCheckDuplException.class);
    }

    @DisplayName("로그인 아이디의 중복 검사를 수행할 수 있다.")
    @Test
    void checkLoginIdDupl(){
        // given
        String loginId = "loginId";
        String email = null;
        String phone = null;

        Teacher teacher = Teacher.builder()
                .loginId(loginId) // 중복
                .loginPw("loginPw")
                .name("테스트강사")
                .email("email")
                .emailVerified(true)
                .phone("010-1234-5678")
                .status(UserStatus.ACTIVE)
                .build();
        teacherCommandRepository.save(teacher);

        // when
        boolean isOk = userQueryService.checkDuplication(loginId, email, phone);

        // then
        assertThat(isOk).isFalse();
    }

    @DisplayName("이메일의 중복 검사를 수행할 수 있다.")
    @Test
    void checkEmailDupl(){
        // given
        String loginId = null;
        String email = "email";
        String phone = null;

        Teacher teacher = Teacher.builder()
                .loginId("loginId")
                .loginPw("loginPw")
                .name("테스트강사")
                .email(email) // 중복
                .emailVerified(true)
                .phone("010-1234-5678")
                .status(UserStatus.ACTIVE)
                .build();
        teacherCommandRepository.save(teacher);

        // when
        boolean isOk = userQueryService.checkDuplication(loginId, email, phone);

        // then
        assertThat(isOk).isFalse();
    }

    @DisplayName("연락처의 중복 검사를 수행할 수 있다.")
    @Test
    void checkPhoneDupl(){
        // given
        String loginId = null;
        String email = null;
        String phone = "010-1234-5678";

        Teacher teacher = Teacher.builder()
                .loginId("loginId")
                .loginPw("loginPw")
                .name("테스트강사")
                .email("email")
                .emailVerified(true)
                .phone(phone) // 중복
                .status(UserStatus.ACTIVE)
                .build();
        teacherCommandRepository.save(teacher);

        // when
        boolean isOk = userQueryService.checkDuplication(loginId, email, phone);

        // then
        assertThat(isOk).isFalse();
    }

    @DisplayName("TRIAL 상태의 사용자는 중복 검사에서 제외된다.")
    @Test
    void ifTrailOk(){
        // given
        String loginId = "loginId";
        String email = null;
        String phone = null;

        Teacher teacher = Teacher.builder()
                .loginId(loginId) // 중복
                .loginPw("loginPw")
                .name("테스트강사")
                .email("email")
                .emailVerified(true)
                .phone("010-1234-5678")
                .status(UserStatus.TRIAL)
                .build();
        teacherCommandRepository.save(teacher);

        // when
        boolean isOk = userQueryService.checkDuplication(loginId, email, phone);

        // then
        assertThat(isOk).isTrue();
    }

    @DisplayName("PENDING 상태의 사용자는 중복 검사에서 제외된다.")
    @Test
    void ifPendingOk(){
        // given
        String loginId = "loginId";
        String email = null;
        String phone = null;

        Teacher teacher = Teacher.builder()
                .loginId(loginId) // 중복
                .loginPw("loginPw")
                .name("테스트강사")
                .email("email")
                .emailVerified(true)
                .phone("010-1234-5678")
                .status(UserStatus.PENDING)
                .build();
        teacherCommandRepository.save(teacher);

        // when
        boolean isOk = userQueryService.checkDuplication(loginId, email, phone);

        // then
        assertThat(isOk).isTrue();
    }

    @DisplayName("이메일 인증을 하지 않은 사용자는 중복 검사에서 제외된다.")
    @Test
    void ifEmailNotVerifiedOk(){
        // given
        String loginId = "loginId";
        String email = null;
        String phone = null;

        Teacher teacher = Teacher.builder()
                .loginId(loginId)
                .loginPw("loginPw")
                .name("테스트강사")
                .email("email")
                .emailVerified(false)
                .phone("010-1234-5678")
                .status(UserStatus.ACTIVE)
                .build();
        teacherCommandRepository.save(teacher);

        // when
        boolean isOk = userQueryService.checkDuplication(loginId, email, phone);

        // then
        assertThat(isOk).isTrue();
    }
}
