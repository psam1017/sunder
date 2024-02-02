package psam.portfolio.sunder.english.service;

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
import psam.portfolio.sunder.english.web.teacher.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.web.teacher.service.AcademyCommandService;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.web.user.exception.DuplicateUserException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
                .detail("detail")
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
                .detail("detail")
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
                .detail("detail")
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
        String teacherUuid = refreshAnd(() -> sut.registerDirectorWithAcademy(academyPOST, directorPOST));

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
        AcademyPOST academyPOST = AcademyPOST.builder()
                .name(uic.getUniqueAcademyName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("street")
                .detail("detail")
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
        String teacherUuid = refreshAnd(() -> sut.registerDirectorWithAcademy(academyPOST, directorPOST));

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
        AcademyPOST academyPOST = AcademyPOST.builder()
                .name(uic.getUniqueAcademyName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("street")
                .detail("detail")
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

        String teacherUuid = sut.registerDirectorWithAcademy(academyPOST, directorPOST);
        Teacher getTeacher = teacherQueryRepository.getById(UUID.fromString(teacherUuid));
        UUID academyUuid = getTeacher.getAcademy().getUuid();

        // when
        Boolean result = refreshAnd(() -> sut.verifyAcademy(academyUuid));

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
        AcademyPOST academyPOST = AcademyPOST.builder()
                .name(uic.getUniqueAcademyName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street("street")
                .detail("detail")
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

        String teacherUuid = sut.registerDirectorWithAcademy(academyPOST, directorPOST);
        Teacher getTeacher = teacherQueryRepository.getById(UUID.fromString(teacherUuid));
        UUID academyUuid = getTeacher.getAcademy().getUuid();
        refreshAnd(() -> sut.verifyAcademy(academyUuid));

        // when
        Boolean result = refreshAnd(() -> sut.verifyAcademy(academyUuid));

        // then
        assertThat(result).isFalse();
    }
}
