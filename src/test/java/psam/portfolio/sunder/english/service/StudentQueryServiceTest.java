package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.exception.AcademyAccessDeniedException;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.student.model.embeddable.Parent;
import psam.portfolio.sunder.english.domain.student.model.embeddable.School;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.student.model.request.StudentSearchCond;
import psam.portfolio.sunder.english.domain.student.model.response.StudentFullResponse;
import psam.portfolio.sunder.english.domain.student.model.response.StudentPublicResponse;
import psam.portfolio.sunder.english.domain.student.service.StudentQueryService;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.*;

@SuppressWarnings("unchecked")
public class StudentQueryServiceTest extends AbstractSunderApplicationTest {

    @Autowired
    StudentQueryService sut; // system under test

    @DisplayName("학생 출석번호에 중복이 있는지 확인할 수 있다.")
    @Test
    void checkDuplication() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);

        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);

        // when
        boolean result = sut.checkDuplication(teacher.getId(), student.getAttendanceId());

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("선생님이 자기 학원의 학생 목록을 조회할 수 있다.")
    @Test
    void getStudentListByTeacher() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);

        List<String> attendanceIds = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
            attendanceIds.add(student.getAttendanceId());
        }

        Collections.sort(attendanceIds);
        attendanceIds.remove(attendanceIds.size() - 1);
        StudentSearchCond cond = StudentSearchCond.builder()
                .page(1)
                .size(10)
                .build();

        // when
        Map<String, Object> result = refreshAnd(() -> sut.getList(teacher.getId(), cond));

        // then
        List<StudentPublicResponse> students = (List<StudentPublicResponse>) result.get("students");
        assertThat(students).hasSize(attendanceIds.size())
                .extracting(StudentPublicResponse::getAttendanceId)
                .containsExactlyElementsOf(attendanceIds);
    }

    @DisplayName("선생님이 특정 조건으로 학생을 검색할 수 있다.")
    @Test
    void searchStudentByTeacher() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);

        // search target
        Student target = dataCreator.registerStudent("Bart Simpson", "HIGH1", new Address("123 Main St", "Apt 4B", "12345"), new School("Springfield Elementary", 4), new Parent("Homer Simpson", "555-123-4567"), UserStatus.ACTIVE, academy);

        // Register students with one different attribute
        dataCreator.registerStudent("Lisa Simpson", "HIGH2", new Address("123 Main St", "Apt 4B", "12345"), new School("Springfield Elementary", 4), new Parent("Homer Simpson", "555-123-4567"), UserStatus.ACTIVE, academy); // Different name
        dataCreator.registerStudent("Bart Simpson", "MID1", new Address("124 Main St", "Apt 4B", "12345"), new School("Springfield Elementary", 4), new Parent("Homer Simpson", "555-123-4567"), UserStatus.ACTIVE, academy); // Different attendanceId
        dataCreator.registerStudent("Bart Simpson", "MID2", new Address("123 Main St", "Apt 4C", "12345"), new School("Springfield Elementary", 4), new Parent("Homer Simpson", "555-123-4567"), UserStatus.ACTIVE, academy); // Different address
        dataCreator.registerStudent("Bart Simpson", "MID3", new Address("123 Main St", "Apt 4B", "12345"), new School("Shelbyville Elementary", 4), new Parent("Homer Simpson", "555-123-4567"), UserStatus.ACTIVE, academy); // Different school name
        dataCreator.registerStudent("Bart Simpson", "ELEM1", new Address("123 Main St", "Apt 4B", "12345"), new School("Springfield Elementary", 5), new Parent("Homer Simpson", "555-123-4567"), UserStatus.ACTIVE, academy); // Different school grade
        dataCreator.registerStudent("Bart Simpson", "ELEM2", new Address("123 Main St", "Apt 4B", "12345"), new School("Springfield Elementary", 4), new Parent("Marge Simpson", "555-123-4567"), UserStatus.ACTIVE, academy); // Different parent name
        dataCreator.registerStudent("Bart Simpson", "ELEM3", new Address("123 Main St", "Apt 4B", "12345"), new School("Springfield Elementary", 4), new Parent("Homer Simpson", "555-123-4567"), UserStatus.WITHDRAWN, academy); // Different status

        StudentSearchCond cond = StudentSearchCond.builder()
                .page(1)
                .size(10)
                .name("Bart")
                .attendanceId("HIGH1")
                .address("123 Main St")
                .status(UserStatus.ACTIVE.toString())
                .schoolName("Springfield Elementary")
                .schoolGrade(4)
                .parentName("Homer")
                .build();

        // when
        Map<String, Object> result = refreshAnd(() -> sut.getList(teacher.getId(), cond));

        // then
        List<StudentPublicResponse> students = (List<StudentPublicResponse>) result.get("students");
        assertThat(students).hasSize(1);
        assertThat(students.get(0).getId()).isEqualTo(target.getId());
        assertThat(students.get(0).getLoginId()).isEqualTo(target.getLoginId());
        assertThat(students.get(0).getName()).isEqualTo(target.getName());
        assertThat(students.get(0).getEmail()).isEqualTo(target.getEmail());
        assertThat(students.get(0).getPhone()).isEqualTo(target.getPhone());
        assertThat(students.get(0).getStreet()).isEqualTo(target.getAddress().getStreet());
        assertThat(students.get(0).getAddressDetail()).isEqualTo(target.getAddress().getDetail());
        assertThat(students.get(0).getPostalCode()).isEqualTo(target.getAddress().getPostalCode());
        assertThat(students.get(0).getStatus()).isEqualTo(target.getStatus());
        assertThat(students.get(0).getAttendanceId()).isEqualTo(target.getAttendanceId());
        assertThat(students.get(0).getSchoolName()).isEqualTo(target.getSchool().getName());
        assertThat(students.get(0).getSchoolGrade()).isEqualTo(target.getSchool().getGrade());
        assertThat(students.get(0).getParentName()).isEqualTo(target.getParent().getName());
        assertThat(students.get(0).getParentPhone()).isEqualTo(target.getParent().getPhone());
    }

    @DisplayName("선생님이 자기 학원 소속의 학생 정보를 조회할 수 있다.")
    @Test
    void getStudentDetailByTeacher() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);

        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, ROLE_STUDENT);

        // when
        StudentFullResponse studentFullResponse = refreshAnd(() -> sut.getDetail(teacher.getId(), student.getId()));

        // then
        assertThat(studentFullResponse.getId()).isEqualTo(student.getId());
        assertThat(studentFullResponse.getLoginId()).isEqualTo(student.getLoginId());
        assertThat(studentFullResponse.getName()).isEqualTo(student.getName());
        assertThat(studentFullResponse.getEmail()).isEqualTo(student.getEmail());
        assertThat(studentFullResponse.getEmailVerified()).isTrue();
        assertThat(studentFullResponse.getPhone()).isEqualTo(student.getPhone());
        assertThat(studentFullResponse.getStreet()).isEqualTo(student.getAddress().getStreet());
        assertThat(studentFullResponse.getAddressDetail()).isEqualTo(student.getAddress().getDetail());
        assertThat(studentFullResponse.getPostalCode()).isEqualTo(student.getAddress().getPostalCode());
        assertThat(studentFullResponse.getStatus()).isEqualTo(student.getStatus());
        assertThat(studentFullResponse.getRoles()).containsExactly(ROLE_STUDENT);
        assertThat(studentFullResponse.getAttendanceId()).isEqualTo(student.getAttendanceId());
        assertThat(studentFullResponse.getSchoolName()).isEqualTo(student.getSchool().getName());
        assertThat(studentFullResponse.getSchoolGrade()).isEqualTo(student.getSchool().getGrade());
        assertThat(studentFullResponse.getParentName()).isEqualTo(student.getParent().getName());
        assertThat(studentFullResponse.getParentPhone()).isEqualTo(student.getParent().getPhone());
        assertThat(studentFullResponse.getAcademyId()).isEqualTo(student.getAcademy().getId());
    }

    @DisplayName("선생님이 다른 학원 소속의 학생 정보를 조회할 수는 없다.")
    @Test
    void getStudentDetailByTeacherWithDifferentAcademy() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);

        Academy anotherAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, anotherAcademy);
        dataCreator.createUserRoles(student, ROLE_STUDENT);

        // when & then
        assertThatThrownBy(() -> sut.getDetail(teacher.getId(), student.getId()))
                .isInstanceOf(AcademyAccessDeniedException.class);
    }
}
