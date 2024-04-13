package psam.portfolio.sunder.english.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.exception.DuplicateAcademyException;
import psam.portfolio.sunder.english.domain.academy.exception.IllegalStatusAcademyException;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.request.AcademyDirectorPOST.AcademyPOST;
import psam.portfolio.sunder.english.domain.academy.model.request.AcademyDirectorPOST.DirectorPOST;
import psam.portfolio.sunder.english.domain.academy.model.request.AcademyPATCH;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.domain.academy.service.AcademyCommandService;
import psam.portfolio.sunder.english.domain.teacher.exception.RoleDirectorRequiredException;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.exception.DuplicateUserException;
import psam.portfolio.sunder.english.domain.user.exception.IllegalStatusUserException;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;
import psam.portfolio.sunder.english.domain.user.model.request.UserLoginForm;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_DIRECTOR;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_TEACHER;

public class AcademyCommandServiceTest extends AbstractSunderApplicationTest {

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
        Academy duplicateAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);

        AcademyPOST academyPOST = AcademyPOST.builder()
                .name(duplicateAcademy.getName())
                .phone(infoContainer.getUniquePhoneNumber())
                .email(infoContainer.getUniqueEmail())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorPOST directorPOST = DirectorPOST.builder()
                .loginId(infoContainer.getUniqueLoginId())
                .loginPw("loginPw")
                .name("name")
                .email(infoContainer.getUniqueEmail())
                .phone(infoContainer.getUniquePhoneNumber())
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
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher duplicateTeacher = dataCreator.registerTeacher(UserStatus.ACTIVE, registerAcademy);

        AcademyPOST academyPOST = AcademyPOST.builder()
                .name(infoContainer.getUniqueAcademyName())
                .phone(infoContainer.getUniquePhoneNumber())
                .email(infoContainer.getUniqueEmail())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorPOST directorPOST = DirectorPOST.builder()
                .loginId(duplicateTeacher.getLoginId())
                .loginPw("loginPw")
                .name("name")
                .email(infoContainer.getUniqueEmail())
                .phone(infoContainer.getUniquePhoneNumber())
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
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.PENDING);
        Teacher registerTeacher = dataCreator.registerTeacher(UserStatus.PENDING, registerAcademy);

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
                .name(infoContainer.getUniqueAcademyName())
                .phone(infoContainer.getUniquePhoneNumber())
                .email(infoContainer.getUniqueEmail())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorPOST directorPOST = DirectorPOST.builder()
                .loginId(infoContainer.getUniqueLoginId())
                .loginPw("loginPw")
                .name("name")
                .email(infoContainer.getUniqueEmail())
                .phone(infoContainer.getUniquePhoneNumber())
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
                .name(infoContainer.getUniqueAcademyName())
                .phone(infoContainer.getUniquePhoneNumber())
                .email(infoContainer.getUniqueEmail())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorPOST directorPOST = DirectorPOST.builder()
                .loginId(infoContainer.getUniqueLoginId())
                .loginPw("loginPw")
                .name("name")
                .email(infoContainer.getUniqueEmail())
                .phone(infoContainer.getUniquePhoneNumber())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .build();

        // when
        UUID teacherId = refreshAnd(() -> sut.registerDirectorWithAcademy(academyPOST, directorPOST));

        // then
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        assertThat(getTeacher.isEmailVerified()).isFalse();

        Academy getAcademy = academyQueryRepository.getById(getTeacher.getAcademy().getId());
        assertThat(getAcademy.isPending()).isTrue();
    }

    @DisplayName("등록된 학원장은 학원장, 교사의 권한을 가진다.")
    @Test
    void hasRoleDirectorAndTeacher() {
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        AcademyPOST academyPOST = AcademyPOST.builder()
                .name(infoContainer.getUniqueAcademyName())
                .phone(infoContainer.getUniquePhoneNumber())
                .email(infoContainer.getUniqueEmail())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorPOST directorPOST = DirectorPOST.builder()
                .loginId(infoContainer.getUniqueLoginId())
                .loginPw("loginPw")
                .name("name")
                .email(infoContainer.getUniqueEmail())
                .phone(infoContainer.getUniquePhoneNumber())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .build();

        // when
        UUID teacherId = refreshAnd(() -> sut.registerDirectorWithAcademy(academyPOST, directorPOST));

        // then
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        assertThat(getTeacher.getRoles()).hasSize(2)
                .extracting(UserRole::getRoleName)
                .containsExactlyInAnyOrder(
                        ROLE_DIRECTOR,
                        ROLE_TEACHER
                );
    }

    @DisplayName("학원 uuid 로 학원과 학원장을 인증할 수 있다.")
    @Test
    void verifyAcademy(){
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        AcademyPOST academyPOST = AcademyPOST.builder()
                .name(infoContainer.getUniqueAcademyName())
                .phone(infoContainer.getUniquePhoneNumber())
                .email(infoContainer.getUniqueEmail())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorPOST directorPOST = DirectorPOST.builder()
                .loginId(infoContainer.getUniqueLoginId())
                .loginPw("loginPw")
                .name("name")
                .email(infoContainer.getUniqueEmail())
                .phone(infoContainer.getUniquePhoneNumber())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .build();

        UUID teacherId = sut.registerDirectorWithAcademy(academyPOST, directorPOST);
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        UUID academyId = getTeacher.getAcademy().getId();

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
                .name(infoContainer.getUniqueAcademyName())
                .phone(infoContainer.getUniquePhoneNumber())
                .email(infoContainer.getUniqueEmail())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .openToPublic(true)
                .build();

        DirectorPOST directorPOST = DirectorPOST.builder()
                .loginId(infoContainer.getUniqueLoginId())
                .loginPw("loginPw")
                .name("name")
                .email(infoContainer.getUniqueEmail())
                .phone(infoContainer.getUniquePhoneNumber())
                .street("street")
                .addressDetail("detail")
                .postalCode("00000")
                .build();

        UUID teacherId = sut.registerDirectorWithAcademy(academyPOST, directorPOST);
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        UUID academyId = getTeacher.getAcademy().getId();
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
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerTeacher = dataCreator.registerTeacher(UserStatus.ACTIVE, registerAcademy);

        String uniqueAcademyName = infoContainer.getUniqueAcademyName();
        String uniquePhoneNumber = infoContainer.getUniquePhoneNumber();
        String uniqueEmail = infoContainer.getUniqueEmail();

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
        UUID academyId = refreshAnd(() -> sut.updateInfo(registerTeacher.getId(), academyPATCH));

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
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerTeacher = dataCreator.registerTeacher(UserStatus.ACTIVE, registerAcademy);

        Academy anotherAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);

        AcademyPATCH academyPATCH = AcademyPATCH.builder()
                .name(anotherAcademy.getName())
                .phone(infoContainer.getUniquePhoneNumber())
                .email(infoContainer.getUniqueEmail())
                .street("new street")
                .addressDetail("new detail")
                .postalCode("11111")
                .openToPublic(false)
                .build();

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.updateInfo(registerTeacher.getId(), academyPATCH)))
                .isInstanceOf(DuplicateAcademyException.class);
    }

    @DisplayName("다른 학원과 전화번호가 중복되면 수정할 수 없다.")
    @Test
    void updateInfoWithDuplicatePhone() {
        // given
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerTeacher = dataCreator.registerTeacher(UserStatus.ACTIVE, registerAcademy);

        Academy anotherAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);

        AcademyPATCH academyPATCH = AcademyPATCH.builder()
                .name(infoContainer.getUniqueAcademyName())
                .phone(anotherAcademy.getPhone())
                .email(infoContainer.getUniqueEmail())
                .street("new street")
                .addressDetail("new detail")
                .postalCode("11111")
                .openToPublic(false)
                .build();

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.updateInfo(registerTeacher.getId(), academyPATCH)))
                .isInstanceOf(DuplicateAcademyException.class);
    }

    @DisplayName("다른 학원과 이메일이 중복되면 수정할 수 없다.")
    @Test
    void updateInfoWithDuplicateEmail() {
        // given
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerTeacher = dataCreator.registerTeacher(UserStatus.ACTIVE, registerAcademy);

        Academy anotherAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);

        AcademyPATCH academyPATCH = AcademyPATCH.builder()
                .name(infoContainer.getUniqueAcademyName())
                .phone(infoContainer.getUniquePhoneNumber())
                .email(anotherAcademy.getEmail())
                .street("new street")
                .addressDetail("new detail")
                .postalCode("11111")
                .openToPublic(false)
                .build();

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.updateInfo(registerTeacher.getId(), academyPATCH)))
                .isInstanceOf(DuplicateAcademyException.class);
    }

    @DisplayName("다른 학원과 정보가 중복되더라도 PENDING 상태는 제외한다.")
    @Test
    void updateInfoWithPending() {
        // given
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerTeacher = dataCreator.registerTeacher(UserStatus.ACTIVE, registerAcademy);

        Academy anotherAcademy = dataCreator.registerAcademy(AcademyStatus.PENDING);

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
        assertThatCode(() -> refreshAnd(() -> sut.updateInfo(registerTeacher.getId(), academyPATCH)))
                .doesNotThrowAnyException();
    }

    // 테스트 6. 학원 정보 수정 시 자기 학원의 이름은 중복 검사에서 제외된다.
    @DisplayName("자기 학원의 이름은 중복 검사에서 제외된다.")
    @Test
    void updateInfoWithSelf() {
        // given
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerTeacher = dataCreator.registerTeacher(UserStatus.ACTIVE, registerAcademy);

        AcademyPATCH academyPATCH = AcademyPATCH.builder()
                .name(registerAcademy.getName())
                .phone(infoContainer.getUniquePhoneNumber())
                .email(infoContainer.getUniqueEmail())
                .street("new street")
                .addressDetail("new detail")
                .postalCode("11111")
                .openToPublic(false)
                .build();

        // when
        // then
        assertThatCode(() -> refreshAnd(() -> sut.updateInfo(registerTeacher.getId(), academyPATCH)))
                .doesNotThrowAnyException();
    }

    @DisplayName("학원장은 자기 학원을 폐쇄 신청할 수 있다.")
    @Test
    void withdraw() {
        // given
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerTeacher = dataCreator.registerTeacher(UserStatus.ACTIVE, registerAcademy);
        dataCreator.createUserRoles(registerTeacher, ROLE_DIRECTOR, ROLE_TEACHER);

        // when
        UUID academyId = refreshAnd(() -> sut.withdraw(registerTeacher.getId()));

        // then
        Academy getAcademy = academyQueryRepository.getById(academyId);
        assertThat(getAcademy.isWithdrawn()).isTrue();
    }

    // 학원장이 아니라면 학원 폐쇄 신청을 할 수 없다.
    @DisplayName("학원장이 아니라면 학원 폐쇄 신청을 할 수 없다.")
    @Test
    void withdrawalRequireRoleDirector() {
        // given
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerTeacher = dataCreator.registerTeacher(UserStatus.ACTIVE, registerAcademy);
        dataCreator.createUserRoles(registerTeacher, ROLE_TEACHER);

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.withdraw(registerTeacher.getId())))
                .isInstanceOf(RoleDirectorRequiredException.class);
    }

    @DisplayName("학원장은 자기 학원의 폐쇄 신청을 취소할 수 있다.")
    @Test
    void revokeWithdraw() {
        // given
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.WITHDRAWN);
        Teacher registerTeacher = dataCreator.registerTeacher(UserStatus.ACTIVE, registerAcademy);
        dataCreator.createUserRoles(registerTeacher, ROLE_DIRECTOR, ROLE_TEACHER);

        // when
        UUID academyId = refreshAnd(() -> sut.revokeWithdrawal(registerTeacher.getId()));

        // then
        Academy getAcademy = academyQueryRepository.getById(academyId);
        assertThat(getAcademy.isWithdrawn()).isFalse();
    }

    @DisplayName("학원장이 아니라면 학원 폐쇄 신청을 취소할 수 없다.")
    @Test
    void revokeWithdrawalRequireRoleDirector() {
        // given
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.WITHDRAWN);
        Teacher registerTeacher = dataCreator.registerTeacher(UserStatus.ACTIVE, registerAcademy);
        dataCreator.createUserRoles(registerTeacher, ROLE_TEACHER);

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.revokeWithdrawal(registerTeacher.getId())))
                .isInstanceOf(RoleDirectorRequiredException.class);
    }

    @DisplayName("사용 체험 중인 학원장이 정규회원으로 전환할 수 있다.")
    @Test
    void fromTrialToActive() {
        // given
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerDirector = dataCreator.registerTeacher(UserStatus.TRIAL, registerAcademy);
        dataCreator.createUserRoles(registerDirector, ROLE_DIRECTOR, ROLE_TEACHER);
        Teacher registerTeacher = dataCreator.registerTeacher(UserStatus.TRIAL, registerAcademy);
        dataCreator.createUserRoles(registerTeacher, ROLE_TEACHER);

        UserLoginForm loginForm = new UserLoginForm(registerDirector.getLoginId(), infoContainer.getAnyRawPassword());

        // when
        Boolean result = refreshAnd(() -> sut.endTrial(loginForm));

        // then
        assertThat(result).isTrue();
        Teacher getDirector = teacherQueryRepository.getById(registerDirector.getId());
        assertThat(getDirector.getStatus()).isEqualTo(UserStatus.ACTIVE);
        Teacher getTeacher = teacherQueryRepository.getById(registerTeacher.getId());
        assertThat(getTeacher.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @DisplayName("사용 체험이 종료된 학원장이 정규회원으로 전환할 수 있다.")
    @Test
    void fromTrialEndToActive() {
        // given
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerDirector = dataCreator.registerTeacher(UserStatus.TRIAL_END, registerAcademy);
        dataCreator.createUserRoles(registerDirector, ROLE_DIRECTOR, ROLE_TEACHER);

        UserLoginForm loginForm = new UserLoginForm(registerDirector.getLoginId(), infoContainer.getAnyRawPassword());

        // when
        Boolean result = refreshAnd(() -> sut.endTrial(loginForm));

        // then
        assertThat(result).isTrue();
        Teacher getDirector = teacherQueryRepository.getById(registerDirector.getId());
        assertThat(getDirector.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @DisplayName("학원장이 아니라면 정규회원으로 전환할 수 없다.")
    @Test
    void endTrialRequireRoleDirector() {
        // given
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerDirector = dataCreator.registerTeacher(UserStatus.TRIAL, registerAcademy);
        dataCreator.createUserRoles(registerDirector, ROLE_TEACHER);

        UserLoginForm loginForm = new UserLoginForm(registerDirector.getLoginId(), infoContainer.getAnyRawPassword());

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.endTrial(loginForm)))
                .isInstanceOf(RoleDirectorRequiredException.class);
    }

    @DisplayName("사용 체험 중이지 않은 학원장은 정규회원으로 전환할 수 없다.")
    @Test
    void endTrialRequireTrial() {
        // given
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher registerDirector = dataCreator.registerTeacher(UserStatus.ACTIVE, registerAcademy);
        dataCreator.createUserRoles(registerDirector, ROLE_DIRECTOR, ROLE_TEACHER);

        UserLoginForm loginForm = new UserLoginForm(registerDirector.getLoginId(), infoContainer.getAnyRawPassword());

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.endTrial(loginForm)))
                .isInstanceOf(IllegalStatusUserException.class);
    }

    @DisplayName("인증되지 않은 학원에서는 학원장이 정규회원으로 전환할 수 없다.")
    @Test
    void endTrialRequireVerifiedAcademy() {
        // given
        Academy registerAcademy = dataCreator.registerAcademy(AcademyStatus.PENDING);
        Teacher registerDirector = dataCreator.registerTeacher(UserStatus.TRIAL, registerAcademy);
        dataCreator.createUserRoles(registerDirector, ROLE_DIRECTOR, ROLE_TEACHER);

        UserLoginForm loginForm = new UserLoginForm(registerDirector.getLoginId(), infoContainer.getAnyRawPassword());

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.endTrial(loginForm)))
                .isInstanceOf(IllegalStatusAcademyException.class);
    }
}
