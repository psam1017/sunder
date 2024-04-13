package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.teacher.exception.SelfRoleModificationException;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPATCHInfo;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPATCHStatus;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPOST;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPOSTRoles;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.domain.teacher.service.TeacherCommandService;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.exception.DuplicateUserException;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class TeacherCommandServiceTest extends AbstractSunderApplicationTest {

    @Autowired
    TeacherCommandService sut;

    @Autowired
    TeacherQueryRepository teacherQueryRepository;

    @Autowired
    PasswordUtils passwordUtils;

    @DisplayName("선생님이 선생님을 등록할 수 있다.")
    @Test
    void registerTeacher(){
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, RoleName.ROLE_DIRECTOR, RoleName.ROLE_TEACHER);

        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);

        String loginId = infoContainer.getUniqueLoginId();
        String password = infoContainer.getAnyRawPassword();
        String name = "name";
        String email = infoContainer.getUniqueEmail();
        String phoneNumber = infoContainer.getUniquePhoneNumber();
        Address address = infoContainer.getAnyAddress();

        TeacherPOST post = new TeacherPOST(
                loginId,
                password,
                name,
                email,
                phoneNumber,
                address.getStreet(),
                address.getDetail(),
                address.getPostalCode()
        );

        // when
        UUID saveTeacherId = refreshAnd(() -> sut.register(director.getId(), post));

        // then
        Teacher getTeacher = teacherQueryRepository.getById(saveTeacherId);
        assertThat(getTeacher.getLoginId()).isEqualTo(loginId);
        assertThat(passwordUtils.matches(password, getTeacher.getLoginPw())).isTrue();
        assertThat(getTeacher.getName()).isEqualTo(name);
        assertThat(getTeacher.getEmail()).isEqualTo(email);
        assertThat(getTeacher.getPhone()).isEqualTo(phoneNumber);
        assertThat(getTeacher.getAddress().getStreet()).isEqualTo(address.getStreet());
        assertThat(getTeacher.getAddress().getDetail()).isEqualTo(address.getDetail());
        assertThat(getTeacher.getAddress().getPostalCode()).isEqualTo(address.getPostalCode());

        // 등록된 선생은 이미 이메일 인증 상태로 취급한다(이메일 인증 생략)
        assertThat(getTeacher.isEmailVerified()).isTrue();

        // 등록된 선생은 ROLE_TEACHER 권한'만' 가진다.
        assertThat(getTeacher.getAcademy().getId()).isEqualTo(academy.getId());
        assertThat(getTeacher.getRoles()).hasSize(1)
                .extracting(UserRole::getRoleName)
                .containsOnly(RoleName.ROLE_TEACHER);
    }

    @DisplayName("학원장이 TRIAL 상태이면 생성한 선생님도 TRIAL 상태로 생성된다.")
    @Test
    void registerTeacherWithTrialStatus() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.TRIAL, academy);
        dataCreator.createUserRoles(director, RoleName.ROLE_DIRECTOR, RoleName.ROLE_TEACHER);

        String loginId = infoContainer.getUniqueLoginId();
        String password = infoContainer.getAnyRawPassword();
        String name = "name";
        String email = infoContainer.getUniqueEmail();
        String phoneNumber = infoContainer.getUniquePhoneNumber();
        Address address = infoContainer.getAnyAddress();

        TeacherPOST post = new TeacherPOST(
                loginId,
                password,
                name,
                email,
                phoneNumber,
                address.getStreet(),
                address.getDetail(),
                address.getPostalCode()
        );

        // when
        UUID saveTeacherId = refreshAnd(() -> sut.register(director.getId(), post));

        // then
        Teacher getTeacher = teacherQueryRepository.getById(saveTeacherId);
        assertThat(getTeacher.getStatus()).isEqualTo(UserStatus.TRIAL);
    }

    @DisplayName("선생님을 등록할 때 선생님 정보에 중복이 있는지 검사할 수 있다.")
    @Test
    void registerTeacherWithDuplicateCheck() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, RoleName.ROLE_DIRECTOR, RoleName.ROLE_TEACHER);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);

        // teacher 와 loginId 가 중복된다.
        String loginId = teacher.getLoginId();
        String password = infoContainer.getAnyRawPassword();
        String name = "name";
        String email = infoContainer.getUniqueEmail();
        String phoneNumber = infoContainer.getUniquePhoneNumber();
        Address address = infoContainer.getAnyAddress();

        TeacherPOST post = new TeacherPOST(
                loginId,
                password,
                name,
                email,
                phoneNumber,
                address.getStreet(),
                address.getDetail(),
                address.getPostalCode()
        );

        // when
        // then
        assertThatThrownBy(() -> sut.register(director.getId(), post))
                .isInstanceOf(DuplicateUserException.class);
    }

    @DisplayName("선생님 정보의 중복 검사 대상에서 PENDING 상태는 제외된다.")
    @Test
    void registerTeacherExcludesPendingStatus() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, RoleName.ROLE_DIRECTOR, RoleName.ROLE_TEACHER);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.PENDING, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);

        // teacher 와 loginId 가 중복되지만 teacher 의 상태가 PENDING 이므로 중복 검사에서 제외된다.
        String loginId = teacher.getLoginId();
        String password = infoContainer.getAnyRawPassword();
        String name = "name";
        String email = infoContainer.getUniqueEmail();
        String phoneNumber = infoContainer.getUniquePhoneNumber();
        Address address = infoContainer.getAnyAddress();

        TeacherPOST post = new TeacherPOST(
                loginId,
                password,
                name,
                email,
                phoneNumber,
                address.getStreet(),
                address.getDetail(),
                address.getPostalCode()
        );

        // when
        // then
        assertThatCode(() -> sut.register(teacher.getId(), post))
                .doesNotThrowAnyException();
    }

    @DisplayName("학원장이 PENDING 상태의 선생님을 ACTIVE 상태로 변경할 수 있다.")
    @Test
    void changeTeacherStatusFromPendingToActive() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, RoleName.ROLE_DIRECTOR, RoleName.ROLE_TEACHER);

        Teacher teacher = dataCreator.registerTeacher(UserStatus.PENDING, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);

        TeacherPATCHStatus patch = new TeacherPATCHStatus(UserStatus.ACTIVE);

        // when
        UserStatus status = refreshAnd(() -> sut.changeStatus(director.getId(), teacher.getId(), patch));

        // then
        assertThat(status).isEqualTo(UserStatus.ACTIVE);
    }

    @DisplayName("학원장이 ACTIVE 상태의 선생님을 WITHDRAWN 상태로 변경할 수 있다.")
    @Test
    void changeTeacherStatusFromActiveToWithdrawn() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, RoleName.ROLE_DIRECTOR, RoleName.ROLE_TEACHER);

        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);

        TeacherPATCHStatus patch = new TeacherPATCHStatus(UserStatus.WITHDRAWN);

        // when
        UserStatus status = refreshAnd(() -> sut.changeStatus(director.getId(), teacher.getId(), patch));

        // then
        assertThat(status).isEqualTo(UserStatus.WITHDRAWN);
    }

    @DisplayName("학원장이 WITHDRAWN 상태의 선생님을 ACTIVE 상태로 변경할 수 있다.")
    @Test
    void changeTeacherStatusFromWithdrawnToActive() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, RoleName.ROLE_DIRECTOR, RoleName.ROLE_TEACHER);

        Teacher teacher = dataCreator.registerTeacher(UserStatus.WITHDRAWN, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);

        TeacherPATCHStatus patch = new TeacherPATCHStatus(UserStatus.ACTIVE);

        // when
        UserStatus status = refreshAnd(() -> sut.changeStatus(director.getId(), teacher.getId(), patch));

        // then
        assertThat(status).isEqualTo(UserStatus.ACTIVE);
    }

    @DisplayName("선생님에게 DIRECTOR 권한을 부여할 수 있다.")
    @Test
    void changeRolesToDirector() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, RoleName.ROLE_DIRECTOR, RoleName.ROLE_TEACHER);

        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);

        HashSet<RoleName> roles = new HashSet<>();
        roles.add(RoleName.ROLE_DIRECTOR);
        roles.add(RoleName.ROLE_TEACHER);
        TeacherPOSTRoles put = new TeacherPOSTRoles(roles);

        // when
        Set<RoleName> saveRoles = refreshAnd(() -> sut.changeRoles(director.getId(), teacher.getId(), put));

        // then
        Teacher getTeacher = teacherQueryRepository.getById(teacher.getId());
        assertThat(getTeacher.getRoles()).hasSize(2)
                .extracting(UserRole::getRoleName)
                .containsExactlyInAnyOrderElementsOf(saveRoles)
                .containsExactlyInAnyOrder(
                        RoleName.ROLE_DIRECTOR,
                        RoleName.ROLE_TEACHER
                );
    }

    @DisplayName("선생님에게 부여된 DIRECTOR 권한을 취소할 수 있다.")
    @Test
    void changeRolesToTeacher() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, RoleName.ROLE_DIRECTOR, RoleName.ROLE_TEACHER);

        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_DIRECTOR, RoleName.ROLE_TEACHER);

        HashSet<RoleName> roles = new HashSet<>();
        roles.add(RoleName.ROLE_TEACHER);
        TeacherPOSTRoles put = new TeacherPOSTRoles(roles);

        // when
        Set<RoleName> saveRoles = refreshAnd(() -> sut.changeRoles(director.getId(), teacher.getId(), put));

        // then
        Teacher getTeacher = teacherQueryRepository.getById(teacher.getId());
        assertThat(getTeacher.getRoles()).hasSize(1)
                .extracting(UserRole::getRoleName)
                .containsExactlyInAnyOrderElementsOf(saveRoles)
                .containsExactlyInAnyOrder(
                        RoleName.ROLE_TEACHER
                );
    }

    @DisplayName("선생님에게 DIRECTOR 권한만 부여할 수 있다.")
    @Test
    void changeRolesToDirectorOnly() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, RoleName.ROLE_DIRECTOR, RoleName.ROLE_TEACHER);

        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);

        HashSet<RoleName> roles = new HashSet<>();
        roles.add(RoleName.ROLE_DIRECTOR);
        TeacherPOSTRoles put = new TeacherPOSTRoles(roles);

        // when
        Set<RoleName> saveRoles = refreshAnd(() -> sut.changeRoles(director.getId(), teacher.getId(), put));

        // then
        Teacher getTeacher = teacherQueryRepository.getById(teacher.getId());
        assertThat(getTeacher.getRoles()).hasSize(1)
                .extracting(UserRole::getRoleName)
                .containsExactlyInAnyOrderElementsOf(saveRoles)
                .containsExactlyInAnyOrder(
                        RoleName.ROLE_DIRECTOR
                );
    }

    @DisplayName("자기 자신의 권한은 변경할 수 없다.")
    @Test
    void changeRolesSelf() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, RoleName.ROLE_DIRECTOR, RoleName.ROLE_TEACHER);

        HashSet<RoleName> roles = new HashSet<>();
        roles.add(RoleName.ROLE_TEACHER);
        TeacherPOSTRoles put = new TeacherPOSTRoles(roles);

        // when
        // then
        assertThatThrownBy(() -> sut.changeRoles(director.getId(), director.getId(), put))
                .isInstanceOf(SelfRoleModificationException.class);
    }

    @DisplayName("선생님이 자신의 정보를 수정할 수 있다.")
    @Test
    void updateTeacher() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);

        String newName = "Alice";
        String newEmail = infoContainer.getUniqueEmail();
        String newPhoneNumber = infoContainer.getUniquePhoneNumber();
        String newStreet = "선더시 선더구 선더로 1";
        String newDetail = "선더빌딩 1층";
        String newPostalCode = "00000";

        // builder 패턴으로 변경
        TeacherPATCHInfo patch = TeacherPATCHInfo.builder()
                .name(newName)
                .phone(newPhoneNumber)
                .email(newEmail)
                .street(newStreet)
                .addressDetail(newDetail)
                .postalCode(newPostalCode)
                .build();

        // when
        UUID updateTeacherId = refreshAnd(() -> sut.updateInfo(teacher.getId(), patch));

        // then
        Teacher getTeacher = teacherQueryRepository.getById(updateTeacherId);
        assertThat(getTeacher.getId()).isEqualTo(teacher.getId());
        assertThat(getTeacher.getName()).isEqualTo(newName);
        assertThat(getTeacher.getEmail()).isEqualTo(newEmail);
        assertThat(getTeacher.getPhone()).isEqualTo(newPhoneNumber);
        assertThat(getTeacher.getAddress().getStreet()).isEqualTo(newStreet);
        assertThat(getTeacher.getAddress().getDetail()).isEqualTo(newDetail);
        assertThat(getTeacher.getAddress().getPostalCode()).isEqualTo(newPostalCode);
    }
}
