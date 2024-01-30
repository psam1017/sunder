package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.SunderApplicationTests;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;
import psam.portfolio.sunder.english.web.teacher.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.web.teacher.exception.DuplicateAcademyException;
import psam.portfolio.sunder.english.web.teacher.exception.OneParamToCheckAcademyDuplException;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;
import psam.portfolio.sunder.english.web.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.web.teacher.model.request.AcademyRegistration;
import psam.portfolio.sunder.english.web.teacher.model.request.DirectorRegistration;
import psam.portfolio.sunder.english.web.teacher.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.web.teacher.service.AcademyCommandService;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.web.user.exception.DuplicateUserException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@SuppressWarnings("ConstantValue")
public class AcademyCommandServiceTest extends SunderApplicationTests {

    @Autowired
    AcademyCommandService sut; // system under test

    @Autowired
    AcademyQueryRepository academyQueryRepository;

    @Autowired
    TeacherQueryRepository teacherQueryRepository;

    @Autowired
    PasswordUtils passwordUtils;

    /*
     * 1. 학원의 정보 중복 검사를 할 때는 하나의 데이터만 전달해야 한다.
     * 2. 학원의 정보 중복 검사를 할 때는 아이디, 이메일, 연락처 중 하나는 반드시 전달해야 한다.
     * 3. 학원 이름의 중복 검사를 수행할 수 있다.
     * 4. 학원의 연락처 중복 검사를 수행할 수 있다.
     * 5. 학원의 이메일 중복 검사를 수행할 수 있다.
     *
     * 6. 학원을 등록할 때도 학원 정보의 중복 검사를 수행한다.
     * 7. 학원을 등록하면서 선생의 정보도 중복 검사를 수행한다.
     * 8. PENDING 상태의 학원은 중복 검사에서 제외된다.
     * 9. 등록된 학원장의 비밀번호는 암호화되어 있다.
     * 10. 등록된 학원과 학원장은 이메일 인증이 필요하다.
     * 12. 학원 uuid 로 학원과 학원장을 인증할 수 있다.
     * 12. 학원은 최초 1회만 인증할 수 있다.
     */

    @DisplayName("학원의 정보 중복 검사를 할 때는 하나의 데이터만 전달해야 한다.")
    @Test
    void oneParamToCheckDuplException(){
        // given
        String name = "name";
        String phone = "";
        String email = "email";

        // when
        // then
        assertThatThrownBy(() -> runWithRefresh(() -> sut.checkDuplication(name, phone, email)))
                .isInstanceOf(OneParamToCheckAcademyDuplException.class);
    }

    @DisplayName("학원의 정보 중복 검사를 할 때는 아이디, 이메일, 연락처 중 하나는 반드시 전달해야 한다.")
    @Test
    void noParamToCheckDuplException(){
        // given
        String name = "";
        String phone = "";
        String email = "";

        // when
        // then
        assertThatThrownBy(() -> runWithRefresh(() -> sut.checkDuplication(name, phone, email)))
                .isInstanceOf(OneParamToCheckAcademyDuplException.class);
    }

    @DisplayName("학원 이름의 중복 검사를 수행할 수 있다.")
    @Test
    void checkNameDupl(){
        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.VERIFIED);

        String name = registerAcademy.getName();
        String phone = null;
        String email = null;

        // when
        boolean isOk = runWithRefresh(() -> sut.checkDuplication(name, phone, email));

        // then
        assertThat(isOk).isFalse();
    }

    @DisplayName("학원의 연락처 중복 검사를 수행할 수 있다.")
    @Test
    void checkPhoneDupl(){
        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.VERIFIED);

        String name = null;
        String phone = registerAcademy.getPhone();
        String email = null;

        // when
        boolean isOk = runWithRefresh(() -> sut.checkDuplication(name, phone, email));

        // then
        assertThat(isOk).isFalse();
    }

    @DisplayName("학원의 이메일 중복 검사를 수행할 수 있다.")
    @Test
    void checkEmailDupl(){
        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.VERIFIED);

        String name = null;
        String phone = null;
        String email = registerAcademy.getEmail();

        // when
        boolean isOk = runWithRefresh(() -> sut.checkDuplication(name, phone, email));

        // then
        assertThat(isOk).isFalse();
    }

    @DisplayName("학원을 등록할 때도 학원 정보의 중복 검사를 수행한다.")
    @Test
    void checkDuplWhenRegisterAcademy(){
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        Academy duplicateAcademy = registerAcademy(AcademyStatus.VERIFIED);

        AcademyRegistration academyRegistration = AcademyRegistration.builder()
                .name(duplicateAcademy.getName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("street")
                .detail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorRegistration directorRegistration = DirectorRegistration.builder()
                .loginId(uic.getUniqueId())
                .loginPw("loginPw")
                .name("name")
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .street("street")
                .detail("detail")
                .postalCode("00000")
                .build();

        // when
        // then
        assertThatThrownBy(() -> runWithRefresh(() -> sut.registerDirectorWithAcademy(academyRegistration, directorRegistration)))
                .isInstanceOf(DuplicateAcademyException.class);
    }

    @DisplayName("학원을 등록하면서 선생의 정보도 중복 검사를 수행한다.")
    @Test
    void checkDuplWhenRegisterDirector(){
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.VERIFIED);
        Teacher duplicateTeacher = registerTeacher(UserStatus.ACTIVE, registerAcademy);

        AcademyRegistration academyRegistration = AcademyRegistration.builder()
                .name(uic.getUniqueAcademyName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("street")
                .detail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorRegistration directorRegistration = DirectorRegistration.builder()
                .loginId(duplicateTeacher.getLoginId())
                .loginPw("loginPw")
                .name("name")
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .street("street")
                .detail("detail")
                .postalCode("00000")
                .build();

        // when
        // then
        assertThatThrownBy(() -> runWithRefresh(() -> sut.registerDirectorWithAcademy(academyRegistration, directorRegistration)))
                .isInstanceOf(DuplicateUserException.class);
    }

    @DisplayName("PENDING 상태의 학원은 중복 검사에서 제외된다.")
    @Test
    void ifPendingOk(){
        // given
        Academy academy = registerAcademy(AcademyStatus.PENDING);

        String name = academy.getName();
        String email = null;
        String phone = null;

        // when
        boolean isOk = runWithRefresh(() -> sut.checkDuplication(name, phone, email));

        // then
        assertThat(isOk).isTrue();
    }

    @DisplayName("등록된 학원장의 비밀번호는 암호화되어 있다.")
    @Test
    void encodeLoginPw(){
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        AcademyRegistration academyRegistration = AcademyRegistration.builder()
                .name(uic.getUniqueAcademyName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("street")
                .detail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorRegistration directorRegistration = DirectorRegistration.builder()
                .loginId(uic.getUniqueId())
                .loginPw("loginPw")
                .name("name")
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .street("street")
                .detail("detail")
                .postalCode("00000")
                .build();

        // when
        String teacherUuid = runWithRefresh(() -> sut.registerDirectorWithAcademy(academyRegistration, directorRegistration));

        // then
        Teacher getTeacher = teacherQueryRepository.getById(UUID.fromString(teacherUuid));
        assertThat(passwordUtils.matches("loginPw", getTeacher.getLoginPw())).isTrue();
    }

    @DisplayName("등록된 학원과 학원장은 이메일 인증이 필요하다.")
    @Test
    void emailVerified(){
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        AcademyRegistration academyRegistration = AcademyRegistration.builder()
                .name(uic.getUniqueAcademyName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("street")
                .detail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorRegistration directorRegistration = DirectorRegistration.builder()
                .loginId(uic.getUniqueId())
                .loginPw("loginPw")
                .name("name")
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .street("street")
                .detail("detail")
                .postalCode("00000")
                .build();

        // when
        String teacherUuid = runWithRefresh(() -> sut.registerDirectorWithAcademy(academyRegistration, directorRegistration));

        // then
        Teacher getTeacher = teacherQueryRepository.getById(UUID.fromString(teacherUuid));
        assertThat(getTeacher.isEmailVerified()).isFalse();

        Academy getAcademy = academyQueryRepository.getById(getTeacher.getAcademy().getUuid());
        assertThat(getAcademy.isPending()).isTrue();
    }

    @DisplayName("학원 uuid 로 학원과 학원장을 인증할 수 있다.")
    @Test
    void verifyAcademy(){
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        AcademyRegistration academyRegistration = AcademyRegistration.builder()
                .name(uic.getUniqueAcademyName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("street")
                .detail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorRegistration directorRegistration = DirectorRegistration.builder()
                .loginId(uic.getUniqueId())
                .loginPw("loginPw")
                .name("name")
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .street("street")
                .detail("detail")
                .postalCode("00000")
                .build();

        String teacherUuid = sut.registerDirectorWithAcademy(academyRegistration, directorRegistration);
        Teacher getTeacher = teacherQueryRepository.getById(UUID.fromString(teacherUuid));
        UUID academyUuid = getTeacher.getAcademy().getUuid();

        // when
        Boolean result = runWithRefresh(() -> sut.verifyAcademy(academyUuid));

        // then
        assertThat(result).isTrue();

        getTeacher = teacherQueryRepository.getById(UUID.fromString(teacherUuid));
        assertThat(getTeacher.isEmailVerified()).isTrue();

        Academy getAcademy = academyQueryRepository.getById(academyUuid);
        assertThat(getAcademy.isVerified()).isTrue();
    }

    @DisplayName("학원은 최초 1회만 인증할 수 있다.")
    @Test
    void verifyAcademyOnlyOnce(){
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        AcademyRegistration academyRegistration = AcademyRegistration.builder()
                .name(uic.getUniqueAcademyName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("street")
                .detail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorRegistration directorRegistration = DirectorRegistration.builder()
                .loginId(uic.getUniqueId())
                .loginPw("loginPw")
                .name("name")
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .street("street")
                .detail("detail")
                .postalCode("00000")
                .build();

        String teacherUuid = sut.registerDirectorWithAcademy(academyRegistration, directorRegistration);
        Teacher getTeacher = teacherQueryRepository.getById(UUID.fromString(teacherUuid));
        UUID academyUuid = getTeacher.getAcademy().getUuid();
        runWithRefresh(() -> sut.verifyAcademy(academyUuid));

        // when
        Boolean result = runWithRefresh(() -> sut.verifyAcademy(academyUuid));

        // then
        assertThat(result).isFalse();
    }
}
