package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPageSearchCond;
import psam.portfolio.sunder.english.domain.teacher.model.response.TeacherFullResponse;
import psam.portfolio.sunder.english.domain.teacher.model.response.TeacherPublicResponse;
import psam.portfolio.sunder.english.domain.teacher.service.TeacherQueryService;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.*;

@SuppressWarnings("unchecked")
public class TeacherQueryServiceTest extends AbstractSunderApplicationTest {

    @Autowired
    TeacherQueryService sut;

    @DisplayName("선생님이 자기 학원의 선생님 목록을 조회할 수 있다.")
    @Test
    void getTeacherListByTeacher(){
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);

        List<Teacher> saveTeachers = new ArrayList<>();
        saveTeachers.add(dataCreator.registerTeacher("Alice", UserStatus.ACTIVE, academy));
        saveTeachers.add(dataCreator.registerTeacher("Andrew", UserStatus.WITHDRAWN, academy));
        saveTeachers.add(dataCreator.registerTeacher("Arnold", UserStatus.FORBIDDEN, academy));
        saveTeachers.add(dataCreator.registerTeacher("Bobby", UserStatus.ACTIVE, academy));
        saveTeachers.add(dataCreator.registerTeacher("Billy", UserStatus.WITHDRAWN, academy));
        saveTeachers.add(dataCreator.registerTeacher("Benny", UserStatus.FORBIDDEN, academy));
        saveTeachers.add(dataCreator.registerTeacher("Cliff", UserStatus.ACTIVE, academy));
        saveTeachers.add(dataCreator.registerTeacher("Cindy", UserStatus.WITHDRAWN, academy));
        saveTeachers.add(dataCreator.registerTeacher("Carter", UserStatus.FORBIDDEN, academy));
        for (Teacher t : saveTeachers) {
            dataCreator.createUserRoles(t, ROLE_TEACHER);
        }

        Teacher director = dataCreator.registerTeacher("Director", UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
        saveTeachers.add(0, director);

        TeacherPageSearchCond cond = TeacherPageSearchCond.builder()
                .page(null)
                .size(null)
                .prop("name")
                .dir("asc")
                .status(UserStatus.ACTIVE.toString())
                .teacherName("A")
                .build();

        // when
        List<?> result = refreshAnd(() -> sut.getList(director.getId(), cond));

        // then
        List<TeacherFullResponse> teacherFullResponses = (List<TeacherFullResponse>) result;
        assertThat(teacherFullResponses).hasSize(1)
                .extracting("name", "status")
                .containsExactly(tuple("Alice", UserStatus.ACTIVE));
    }

    @DisplayName("학생이 자기 학원의 선생님 목록을 조회할 수 있다.")
    @Test
    void getTeacherListByStudent(){
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);

        List<Teacher> saveTeachers = new ArrayList<>();
        saveTeachers.add(dataCreator.registerTeacher("Alice", UserStatus.ACTIVE, academy));
        saveTeachers.add(dataCreator.registerTeacher("Andrew", UserStatus.WITHDRAWN, academy));
        saveTeachers.add(dataCreator.registerTeacher("Arnold", UserStatus.FORBIDDEN, academy));
        saveTeachers.add(dataCreator.registerTeacher("Bobby", UserStatus.ACTIVE, academy));
        saveTeachers.add(dataCreator.registerTeacher("Billy", UserStatus.WITHDRAWN, academy));
        saveTeachers.add(dataCreator.registerTeacher("Benny", UserStatus.FORBIDDEN, academy));
        saveTeachers.add(dataCreator.registerTeacher("Cliff", UserStatus.ACTIVE, academy));
        saveTeachers.add(dataCreator.registerTeacher("Cindy", UserStatus.WITHDRAWN, academy));
        saveTeachers.add(dataCreator.registerTeacher("Carter", UserStatus.FORBIDDEN, academy));
        for (Teacher t : saveTeachers) {
            dataCreator.createUserRoles(t, ROLE_TEACHER);
        }

        Teacher director = dataCreator.registerTeacher("Director", UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
        saveTeachers.add(0, director);

        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, ROLE_STUDENT);

        TeacherPageSearchCond cond = TeacherPageSearchCond.builder()
                .page(null)
                .size(null)
                .prop("name")
                .dir("asc")
                .status(UserStatus.ACTIVE.toString())
                .teacherName("A")
                .build();

        // when
        List<?> result = refreshAnd(() -> sut.getList(student.getId(), cond));

        // then
        List<TeacherPublicResponse> teacherPublicResponses = (List<TeacherPublicResponse>) result;
        assertThat(teacherPublicResponses).hasSize(1)
                .extracting("name", "status")
                .containsExactly(tuple("Alice", UserStatus.ACTIVE));
    }

    @DisplayName("선생님이 선생님을 상세 조회할 수 있다.")
    @Test
    void getTeacherDetailByTeacher(){
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        // when
        Object result = refreshAnd(() -> sut.getDetail(director.getId(), director.getId()));

        // then
        TeacherFullResponse teacherFullResponse = (TeacherFullResponse) result;
        assertThat(teacherFullResponse.getId()).isEqualTo(director.getId());
        assertThat(teacherFullResponse.getName()).isEqualTo(director.getName());
        assertThat(teacherFullResponse.getEmail()).isEqualTo(director.getEmail());
        assertThat(teacherFullResponse.getPhone()).isEqualTo(director.getPhone());
        assertThat(teacherFullResponse.getStreet()).isEqualTo(academy.getAddress().getStreet());
        assertThat(teacherFullResponse.getAddressDetail()).isEqualTo(academy.getAddress().getDetail());
        assertThat(teacherFullResponse.getPostalCode()).isEqualTo(academy.getAddress().getPostalCode());
        assertThat(teacherFullResponse.getStatus()).isEqualTo(director.getStatus());
        assertThat(teacherFullResponse.getRoles()).containsExactlyInAnyOrder(ROLE_DIRECTOR, ROLE_TEACHER);
        assertThat(teacherFullResponse.getAcademyId()).isEqualTo(director.getAcademy().getId());
    }

    @DisplayName("학생이 선생님을 상세 조회할 수 있다.")
    @Test
    void getTeacherDetailByStudent(){
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, ROLE_STUDENT);

        // when
        Object result = refreshAnd(() -> sut.getDetail(student.getId(), director.getId()));

        // then
        TeacherPublicResponse teacherPublicResponse = (TeacherPublicResponse) result;
        assertThat(teacherPublicResponse.getId()).isEqualTo(director.getId());
        assertThat(teacherPublicResponse.getName()).isEqualTo(director.getName());
        assertThat(teacherPublicResponse.getStatus()).isEqualTo(director.getStatus());
        assertThat(teacherPublicResponse.getRoles()).containsExactlyInAnyOrder(ROLE_DIRECTOR, ROLE_TEACHER);
    }
}
