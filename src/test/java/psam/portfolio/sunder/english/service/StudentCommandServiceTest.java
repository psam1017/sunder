package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.student.exception.DuplicateAttendanceIdException;
import psam.portfolio.sunder.english.domain.student.model.embeddable.Parent;
import psam.portfolio.sunder.english.domain.student.model.embeddable.School;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.student.model.request.StudentPATCHInfo;
import psam.portfolio.sunder.english.domain.student.model.request.StudentPATCHStatus;
import psam.portfolio.sunder.english.domain.student.model.request.StudentPOST;
import psam.portfolio.sunder.english.domain.student.repository.StudentQueryRepository;
import psam.portfolio.sunder.english.domain.student.service.StudentCommandService;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.exception.DuplicateUserException;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;
import psam.portfolio.sunder.english.domain.user.service.UserQueryService;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_DIRECTOR;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_TEACHER;

class StudentCommandServiceTest extends AbstractSunderApplicationTest {

    @Autowired
    StudentCommandService sut; // system under test

    @Autowired
    UserQueryService userQueryService;

    @Autowired
    StudentQueryRepository studentQueryRepository;

    @Autowired
    TeacherQueryRepository teacherQueryRepository;

    @Autowired
    PasswordUtils passwordUtils;

    // 끝에 있는 선생님 테스트의 학생 버전
    @DisplayName("선생님이 학생을 등록할 수 있다.")
    @Test
    void registerStudent() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_DIRECTOR, ROLE_TEACHER);

        String loginId = infoContainer.getUniqueLoginId();
        String password = infoContainer.getAnyRawPassword();
        String name = "선더학생";
        String email = infoContainer.getUniqueEmail();
        String phoneNumber = infoContainer.getUniquePhoneNumber();
        Address address = infoContainer.getAnyAddress();
        String attendanceId = infoContainer.getUniqueAttendanceId();
        String note = "note about student";
        School school = infoContainer.getAnySchool();
        Parent parent = infoContainer.getAnyParent();

        StudentPOST post = new StudentPOST(
                loginId,
                password,
                name,
                email,
                phoneNumber,
                address.getStreet(),
                address.getDetail(),
                address.getPostalCode(),
                attendanceId,
                note,
                school.getName(),
                school.getGrade(),
                parent.getName(),
                parent.getPhone()
        );

        // when
        UUID saveStudentId = refreshAnd(() -> sut.register(teacher.getId(), post));

        // then
        Student getStudent = studentQueryRepository.getById(saveStudentId);
        assertThat(getStudent.getLoginId()).isEqualTo(loginId);
        assertThat(passwordUtils.matches(password, getStudent.getLoginPw())).isTrue();
        assertThat(getStudent.getName()).isEqualTo(name);
        assertThat(getStudent.getEmail()).isEqualTo(email);
        assertThat(getStudent.getPhone()).isEqualTo(phoneNumber);
        assertThat(getStudent.getAddress().getStreet()).isEqualTo(address.getStreet());
        assertThat(getStudent.getAddress().getDetail()).isEqualTo(address.getDetail());
        assertThat(getStudent.getAddress().getPostalCode()).isEqualTo(address.getPostalCode());
        assertThat(getStudent.getAttendanceId()).isEqualTo(attendanceId);
        assertThat(getStudent.getNote()).isEqualTo(note);
        assertThat(getStudent.getSchool().getName()).isEqualTo(school.getName());
        assertThat(getStudent.getSchool().getGrade()).isEqualTo(school.getGrade());
        assertThat(getStudent.getParent().getName()).isEqualTo(parent.getName());
        assertThat(getStudent.getParent().getPhone()).isEqualTo(parent.getPhone());

        // 등록된 학생은 이미 이메일 인증 상태로 취급한다(이메일 인증 생략)
        assertThat(getStudent.isEmailVerified()).isTrue();

        // 등록된 학생은 ROLE_STUDENT 권한'만' 가진다.
        assertThat(getStudent.getAcademy().getId()).isEqualTo(academy.getId());
        assertThat(getStudent.getRoles()).hasSize(1)
                .extracting(UserRole::getRoleName)
                .containsOnly(RoleName.ROLE_STUDENT);
    }

    @DisplayName("학원장이 TRIAL 상태이면 생성한 학생도 TRIAL 상태로 생성된다.")
    @Test
    void registerStudentWithTrialStatus() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.TRIAL, academy);
        dataCreator.createUserRoles(teacher, ROLE_DIRECTOR, ROLE_TEACHER);

        String loginId = infoContainer.getUniqueLoginId();
        String password = infoContainer.getAnyRawPassword();
        String name = "선더학생";
        String email = infoContainer.getUniqueEmail();
        String phoneNumber = infoContainer.getUniquePhoneNumber();
        Address address = infoContainer.getAnyAddress();
        String attendanceId = infoContainer.getUniqueAttendanceId();
        String note = "note about student";
        School school = infoContainer.getAnySchool();
        Parent parent = infoContainer.getAnyParent();

        StudentPOST post = new StudentPOST(
                loginId,
                password,
                name,
                email,
                phoneNumber,
                address.getStreet(),
                address.getDetail(),
                address.getPostalCode(),
                attendanceId,
                note,
                school.getName(),
                school.getGrade(),
                parent.getName(),
                parent.getPhone()
        );

        // when
        UUID saveStudentId = refreshAnd(() -> sut.register(teacher.getId(), post));

        // then
        Student getStudent = studentQueryRepository.getById(saveStudentId);
        assertThat(getStudent.getStatus()).isEqualTo(UserStatus.TRIAL);
    }

    @DisplayName("학생을 등록할 때 학생 정보에 중복이 있는지 검사할 수 있다.")
    @Test
    void registerStudentWithDuplicateCheck() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_DIRECTOR, ROLE_TEACHER);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        // student 와 loginId 가 중복된다.
        String loginId = student.getLoginId();

        String password = infoContainer.getAnyRawPassword();
        String name = "선더학생";
        String email = infoContainer.getUniqueEmail();
        String phoneNumber = infoContainer.getUniquePhoneNumber();
        Address address = infoContainer.getAnyAddress();
        String attendanceId = infoContainer.getUniqueAttendanceId();
        String note = "note about student";
        School school = infoContainer.getAnySchool();
        Parent parent = infoContainer.getAnyParent();

        StudentPOST post = new StudentPOST(
                loginId,
                password,
                name,
                email,
                phoneNumber,
                address.getStreet(),
                address.getDetail(),
                address.getPostalCode(),
                attendanceId,
                note,
                school.getName(),
                school.getGrade(),
                parent.getName(),
                parent.getPhone()
        );

        // when
        // then
        assertThatThrownBy(() -> sut.register(teacher.getId(), post))
                .isInstanceOf(DuplicateUserException.class);
    }

    @DisplayName("학생을 등록할 때 출석번호에 중복이 있는지 검사할 수 있다.")
    @Test
    void registerStudentWithDuplicateAttendanceIdCheck() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_DIRECTOR, ROLE_TEACHER);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        // student 와 attendanceId 가 중복된다.
        String attendanceId = student.getAttendanceId();

        String loginId = infoContainer.getUniqueLoginId();
        String password = infoContainer.getAnyRawPassword();
        String name = "선더학생";
        String email = infoContainer.getUniqueEmail();
        String phoneNumber = infoContainer.getUniquePhoneNumber();
        Address address = infoContainer.getAnyAddress();
        String note = "note about student";
        School school = infoContainer.getAnySchool();
        Parent parent = infoContainer.getAnyParent();

        StudentPOST post = new StudentPOST(
                loginId,
                password,
                name,
                email,
                phoneNumber,
                address.getStreet(),
                address.getDetail(),
                address.getPostalCode(),
                attendanceId,
                note,
                school.getName(),
                school.getGrade(),
                parent.getName(),
                parent.getPhone()
        );

        // when
        // then
        assertThatThrownBy(() -> sut.register(teacher.getId(), post))
                .isInstanceOf(DuplicateAttendanceIdException.class);
    }

    @DisplayName("학생 정보의 중복 검사 대상에서 PENDING 상태는 제외된다.")
    @Test
    void registerStudentExcludesPendingStatus() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_DIRECTOR, ROLE_TEACHER);
        Student student = dataCreator.registerStudent(UserStatus.PENDING, academy);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        // student 와 loginId 가 중복되지만 student 의 상태가 PENDING 이므로 중복 검사에서 제외된다.
        String loginId = student.getLoginId();
        String password = infoContainer.getAnyRawPassword();
        String name = "선더학생";
        String email = infoContainer.getUniqueEmail();
        String phoneNumber = infoContainer.getUniquePhoneNumber();
        Address address = infoContainer.getAnyAddress();
        String attendanceId = infoContainer.getUniqueAttendanceId();
        String note = "note about student";
        School school = infoContainer.getAnySchool();
        Parent parent = infoContainer.getAnyParent();

        StudentPOST post = new StudentPOST(
                loginId,
                password,
                name,
                email,
                phoneNumber,
                address.getStreet(),
                address.getDetail(),
                address.getPostalCode(),
                attendanceId,
                note,
                school.getName(),
                school.getGrade(),
                parent.getName(),
                parent.getPhone()
        );

        // when
        // then
        assertThatCode(() -> sut.register(teacher.getId(), post))
                .doesNotThrowAnyException();
    }

    // 선생님이 학생의 정보를 수정할 수 있다.
    @DisplayName("선생님이 학생의 정보를 수정할 수 있다.")
    @Test
    void updateStudentInfo() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_DIRECTOR, ROLE_TEACHER);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        // when
        String updatedName = "수정된 선더학생";
        String updatedEmail = infoContainer.getUniqueEmail();
        String updatedPhoneNumber = infoContainer.getUniquePhoneNumber();
        Address updatedAddress = Address.builder()
                .street("서울특별시 선더구 수정된 선더로 1")
                .detail("수정된 선더빌딩")
                .postalCode("11111")
                .build();
        String updatedAttendanceId = infoContainer.getUniqueAttendanceId();
        String updatedNote = "updated note about student";
        School updatedSchool = School.builder()
                .name("수정된 선더초등학교")
                .grade(4)
                .build();
        Parent updatedParent = Parent.builder()
                .name("수정된 선더부모")
                .phone("01087654321")
                .build();

        StudentPATCHInfo patch = StudentPATCHInfo.builder()
                .name(updatedName)
                .phone(updatedPhoneNumber)
                .email(updatedEmail)
                .street(updatedAddress.getStreet())
                .addressDetail(updatedAddress.getDetail())
                .postalCode(updatedAddress.getPostalCode())
                .attendanceId(updatedAttendanceId)
                .note(updatedNote)
                .schoolName(updatedSchool.getName())
                .schoolGrade(updatedSchool.getGrade())
                .parentName(updatedParent.getName())
                .parentPhone(updatedParent.getPhone())
                .build();

        // when
        UUID updatedStudentId = refreshAnd(() -> sut.updateInfo(teacher.getId(), student.getId(), patch));

        // then
        Student getStudent = studentQueryRepository.getById(updatedStudentId);
        assertThat(getStudent.getName()).isEqualTo(updatedName);
        assertThat(getStudent.getPhone()).isEqualTo(updatedPhoneNumber);
        assertThat(getStudent.getEmail()).isEqualTo(updatedEmail);
        assertThat(getStudent.getAddress().getStreet()).isEqualTo(updatedAddress.getStreet());
        assertThat(getStudent.getAddress().getDetail()).isEqualTo(updatedAddress.getDetail());
        assertThat(getStudent.getAddress().getPostalCode()).isEqualTo(updatedAddress.getPostalCode());
        assertThat(getStudent.getAttendanceId()).isEqualTo(updatedAttendanceId);
        assertThat(getStudent.getNote()).isEqualTo(updatedNote);
        assertThat(getStudent.getSchool().getName()).isEqualTo(updatedSchool.getName());
        assertThat(getStudent.getSchool().getGrade()).isEqualTo(updatedSchool.getGrade());
        assertThat(getStudent.getParent().getName()).isEqualTo(updatedParent.getName());
        assertThat(getStudent.getParent().getPhone()).isEqualTo(updatedParent.getPhone());
    }

    @DisplayName("선생님이 PENDING 상태의 학생을 ACTIVE 상태로 변경할 수 있다.")
    @Test
    void changeStudentStatusFromPendingToActive() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_DIRECTOR, ROLE_TEACHER);
        Student student = dataCreator.registerStudent(UserStatus.PENDING, academy);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        StudentPATCHStatus patch = StudentPATCHStatus.builder()
                .status(UserStatus.ACTIVE)
                .build();

        // when
        UserStatus updatedStatus = sut.changeStatus(teacher.getId(), student.getId(), patch);

        // then
        Student getStudent = studentQueryRepository.getById(student.getId());
        assertThat(getStudent.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(updatedStatus).isEqualTo(UserStatus.ACTIVE);
    }

    @DisplayName("선생님이 ACTIVE 상태의 학생을 WITHDRAWN 상태로 변경할 수 있다.")
    @Test
    void changeStudentStatusFromActiveToWithdrawn() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_DIRECTOR, ROLE_TEACHER);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        StudentPATCHStatus patch = StudentPATCHStatus.builder()
                .status(UserStatus.WITHDRAWN)
                .build();

        // when
        UserStatus updatedStatus = sut.changeStatus(teacher.getId(), student.getId(), patch);

        // then
        Student getStudent = studentQueryRepository.getById(student.getId());
        assertThat(getStudent.getStatus()).isEqualTo(UserStatus.WITHDRAWN);
        assertThat(updatedStatus).isEqualTo(UserStatus.WITHDRAWN);
    }

    @DisplayName("선생님이 WITHDRAWN 상태의 학생을 ACTIVE 상태로 변경할 수 있다.")
    @Test
    void changeStudentStatusFromWithdrawnToActive() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_DIRECTOR, ROLE_TEACHER);
        Student student = dataCreator.registerStudent(UserStatus.WITHDRAWN, academy);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        StudentPATCHStatus patch = StudentPATCHStatus.builder()
                .status(UserStatus.ACTIVE)
                .build();

        // when
        UserStatus updatedStatus = sut.changeStatus(teacher.getId(), student.getId(), patch);

        // then
        Student getStudent = studentQueryRepository.getById(student.getId());
        assertThat(getStudent.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(updatedStatus).isEqualTo(UserStatus.ACTIVE);
    }
}
