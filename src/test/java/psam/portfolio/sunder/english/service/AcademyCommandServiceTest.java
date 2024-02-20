package psam.portfolio.sunder.english.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.SunderApplicationTests;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;
import psam.portfolio.sunder.english.web.teacher.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.web.teacher.exception.DuplicateAcademyException;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;
import psam.portfolio.sunder.english.web.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.web.teacher.model.request.AcademyDirectorPOST.DirectorPOST;
import psam.portfolio.sunder.english.web.teacher.model.request.AcademyDirectorPOST.AcademyPOST;
import psam.portfolio.sunder.english.web.teacher.model.request.AcademyPATCH;
import psam.portfolio.sunder.english.web.teacher.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.web.teacher.service.AcademyCommandService;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.web.user.exception.DuplicateUserException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

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

    @DisplayName("학원을 등록할 때도 학원 정보의 중복 검사를 수행한다.")
    @Test
    void checkDuplWhenRegisterAcademy(){
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        Academy duplicateAcademy = registerAcademy(AcademyStatus.VERIFIED);

        AcademyPOST academyPOST = AcademyPOST.builder()
                .name(duplicateAcademy.getName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorPOST directorPOST = DirectorPOST.builder()
                .loginId(uic.getUniqueLoginId())
                .loginPw("loginPw")
                .name("name")
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .build();

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.registerDirectorWithAcademy(academyPOST, directorPOST)))
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

        AcademyPOST academyPOST = AcademyPOST.builder()
                .name(uic.getUniqueAcademyName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorPOST directorPOST = DirectorPOST.builder()
                .loginId(duplicateTeacher.getLoginId())
                .loginPw("loginPw")
                .name("name")
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .build();

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.registerDirectorWithAcademy(academyPOST, directorPOST)))
                .isInstanceOf(DuplicateUserException.class);
    }

    @DisplayName("중복 검사 대상에서 PENDING 상태는 제외된다.")
    @Test
    void excludePending(){
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.PENDING);
        Teacher registerTeacher = registerTeacher(UserStatus.PENDING, registerAcademy);

        AcademyPOST academyPOST = AcademyPOST.builder()
                .name(registerAcademy.getName()) // PENDING 상태인 학원의 이름
                .phone(null) // null
                .email(null) // null
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorPOST directorPOST = DirectorPOST.builder()
                .loginId(registerTeacher.getLoginId())
                .loginPw("P@ssw0rd!")
                .name("name")
                .email(registerTeacher.getEmail())
                .phone(null)
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .build();

        // when
        // then
        Assertions.assertThatCode(() -> sut.registerDirectorWithAcademy(academyPOST, directorPOST))
                .doesNotThrowAnyException();
    }

    @DisplayName("등록된 학원장의 비밀번호는 암호화되어 있다.")
    @Test
    void encodeLoginPw(){
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        AcademyPOST academyPOST = AcademyPOST.builder()
                .name(uic.getUniqueAcademyName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorPOST directorPOST = DirectorPOST.builder()
                .loginId(uic.getUniqueLoginId())
                .loginPw("loginPw")
                .name("name")
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .build();

        // when
        UUID teacherId = refreshAnd(() -> sut.registerDirectorWithAcademy(academyPOST, directorPOST));

        // then
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        assertThat(passwordUtils.matches("loginPw", getTeacher.getLoginPw())).isTrue();
    }

    @DisplayName("등록된 학원과 학원장은 이메일 인증이 필요하다.")
    @Test
    void emailVerified(){
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        AcademyPOST academyPOST = AcademyPOST.builder()
                .name(uic.getUniqueAcademyName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorPOST directorPOST = DirectorPOST.builder()
                .loginId(uic.getUniqueLoginId())
                .loginPw("loginPw")
                .name("name")
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .build();

        // when
        UUID teacherId = refreshAnd(() -> sut.registerDirectorWithAcademy(academyPOST, directorPOST));

        // then
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
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
        AcademyPOST academyPOST = AcademyPOST.builder()
                .name(uic.getUniqueAcademyName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorPOST directorPOST = DirectorPOST.builder()
                .loginId(uic.getUniqueLoginId())
                .loginPw("loginPw")
                .name("name")
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .build();

        UUID teacherId = sut.registerDirectorWithAcademy(academyPOST, directorPOST);
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        UUID academyId = getTeacher.getAcademy().getUuid();

        // when
        Boolean result = refreshAnd(() -> sut.verify(academyId));

        // then
        assertThat(result).isTrue();

        getTeacher = teacherQueryRepository.getById(teacherId);
        assertThat(getTeacher.isEmailVerified()).isTrue();

        Academy getAcademy = academyQueryRepository.getById(academyId);
        assertThat(getAcademy.isVerified()).isTrue();
    }

    @DisplayName("학원은 최초 1회만 인증할 수 있다.")
    @Test
    void verifyAcademyOnlyOnce(){
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        AcademyPOST academyPOST = AcademyPOST.builder()
                .name(uic.getUniqueAcademyName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorPOST directorPOST = DirectorPOST.builder()
                .loginId(uic.getUniqueLoginId())
                .loginPw("loginPw")
                .name("name")
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .build();

        UUID teacherId = sut.registerDirectorWithAcademy(academyPOST, directorPOST);
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        UUID academyId = getTeacher.getAcademy().getUuid();
        refreshAnd(() -> sut.verify(academyId));

        // when
        Boolean result = refreshAnd(() -> sut.verify(academyId));

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("학원장은 자기 학원의 정보를 수정할 수 있다.")
    @Test
    void updateInfo() {
        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerTeacher = registerTeacher(UserStatus.ACTIVE, registerAcademy);

        String uniqueAcademyName = uic.getUniqueAcademyName();
        String uniquePhoneNumber = uic.getUniquePhoneNumber();
        String uniqueEmail = uic.getUniqueEmail();

        AcademyPATCH academyPATCH = AcademyPATCH.builder()
                .name(uniqueAcademyName)
                .phone(uniquePhoneNumber)
                .email(uniqueEmail)
                .street("new street")
                .addressDetail("new detail")
                .postalCode("11111")
                .openToPublic(false)
                .build();

        // when
        UUID academyId = refreshAnd(() -> sut.updateInfo(registerTeacher.getUuid(), academyPATCH));

        // then
        Academy getAcademy = academyQueryRepository.getById(academyId);
        assertThat(getAcademy.getName()).isEqualTo(uniqueAcademyName);
        assertThat(getAcademy.getPhone()).isEqualTo(uniquePhoneNumber);
        assertThat(getAcademy.getEmail()).isEqualTo(uniqueEmail);
        assertThat(getAcademy.getAddress().getStreet()).isEqualTo("new street");
        assertThat(getAcademy.getAddress().getDetail()).isEqualTo("new detail");
        assertThat(getAcademy.getAddress().getPostalCode()).isEqualTo("11111");
        assertThat(getAcademy.isOpenToPublic()).isFalse();
    }

    @DisplayName("다른 학원과 이름이 중복되면 수정할 수 없다.")
    @Test
    void updateInfoWithDuplicateName() {
        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerTeacher = registerTeacher(UserStatus.ACTIVE, registerAcademy);

        Academy anotherAcademy = registerAcademy(AcademyStatus.VERIFIED);

        AcademyPATCH academyPATCH = AcademyPATCH.builder()
                .name(anotherAcademy.getName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("new street")
                .addressDetail("new detail")
                .postalCode("11111")
                .openToPublic(false)
                .build();

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.updateInfo(registerTeacher.getUuid(), academyPATCH)))
                .isInstanceOf(DuplicateAcademyException.class);
    }

    @DisplayName("다른 학원과 전화번호가 중복되면 수정할 수 없다.")
    @Test
    void updateInfoWithDuplicatePhone() {
        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerTeacher = registerTeacher(UserStatus.ACTIVE, registerAcademy);

        Academy anotherAcademy = registerAcademy(AcademyStatus.VERIFIED);

        AcademyPATCH academyPATCH = AcademyPATCH.builder()
                .name(uic.getUniqueAcademyName())
                .phone(anotherAcademy.getPhone())
                .email(uic.getUniqueEmail())
                .street("new street")
                .addressDetail("new detail")
                .postalCode("11111")
                .openToPublic(false)
                .build();

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.updateInfo(registerTeacher.getUuid(), academyPATCH)))
                .isInstanceOf(DuplicateAcademyException.class);
    }

    @DisplayName("다른 학원과 이메일이 중복되면 수정할 수 없다.")
    @Test
    void updateInfoWithDuplicateEmail() {
        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerTeacher = registerTeacher(UserStatus.ACTIVE, registerAcademy);

        Academy anotherAcademy = registerAcademy(AcademyStatus.VERIFIED);

        AcademyPATCH academyPATCH = AcademyPATCH.builder()
                .name(uic.getUniqueAcademyName())
                .phone(uic.getUniquePhoneNumber())
                .email(anotherAcademy.getEmail())
                .street("new street")
                .addressDetail("new detail")
                .postalCode("11111")
                .openToPublic(false)
                .build();

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.updateInfo(registerTeacher.getUuid(), academyPATCH)))
                .isInstanceOf(DuplicateAcademyException.class);
    }

    @DisplayName("다른 학원과 정보가 중복되더라도 PENDING 상태는 제외한다.")
    @Test
    void updateInfoWithPending() {
        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerTeacher = registerTeacher(UserStatus.ACTIVE, registerAcademy);

        Academy anotherAcademy = registerAcademy(AcademyStatus.PENDING);

        AcademyPATCH academyPATCH = AcademyPATCH.builder()
                .name(anotherAcademy.getName())
                .phone(anotherAcademy.getPhone())
                .email(anotherAcademy.getEmail())
                .street("new street")
                .addressDetail("new detail")
                .postalCode("11111")
                .openToPublic(false)
                .build();

        // when
        // then
        assertThatCode(() -> refreshAnd(() -> sut.updateInfo(registerTeacher.getUuid(), academyPATCH)))
                .doesNotThrowAnyException();
    }

    // 테스트 6. 학원 정보 수정 시 자기 학원의 이름은 중복 검사에서 제외된다.
    @DisplayName("자기 학원의 이름은 중복 검사에서 제외된다.")
    @Test
    void updateInfoWithSelf() {
        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerTeacher = registerTeacher(UserStatus.ACTIVE, registerAcademy);

        AcademyPATCH academyPATCH = AcademyPATCH.builder()
                .name(registerAcademy.getName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("new street")
                .addressDetail("new detail")
                .postalCode("11111")
                .openToPublic(false)
                .build();

        // when
        // then
        assertThatCode(() -> refreshAnd(() -> sut.updateInfo(registerTeacher.getUuid(), academyPATCH)))
                .doesNotThrowAnyException();
    }
}
