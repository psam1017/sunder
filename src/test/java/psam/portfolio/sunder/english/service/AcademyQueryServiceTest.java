package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.SunderApplicationTests;
import psam.portfolio.sunder.english.global.pagination.PageInfo;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.exception.OneParamToCheckAcademyDuplException;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.academy.model.request.AcademyPublicSearchCond;
import psam.portfolio.sunder.english.domain.academy.model.response.AcademyFullResponse;
import psam.portfolio.sunder.english.domain.teacher.model.response.TeacherFullResponse;
import psam.portfolio.sunder.english.domain.academy.service.AcademyQueryService;
import psam.portfolio.sunder.english.domain.teacher.model.response.TeacherPublicResponse;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.*;

@SuppressWarnings({"unchecked", "ConstantValue"})
public class AcademyQueryServiceTest extends SunderApplicationTests {

    @Autowired
    AcademyQueryService sut; // system under test

    @DisplayName("학원의 정보 중복 검사를 할 때는 하나의 데이터만 전달해야 한다.")
    @Test
    void oneParamToCheckDuplException() {
        // given
        String name = "name";
        String phone = "";
        String email = "email";

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.checkDuplication(name, phone, email)))
                .isInstanceOf(OneParamToCheckAcademyDuplException.class);
    }

    @DisplayName("학원의 정보 중복 검사를 할 때는 아이디, 이메일, 연락처 중 하나는 반드시 전달해야 한다.")
    @Test
    void noParamToCheckDuplException() {
        // given
        String name = "";
        String phone = "";
        String email = "";

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.checkDuplication(name, phone, email)))
                .isInstanceOf(OneParamToCheckAcademyDuplException.class);
    }

    @DisplayName("학원 이름의 중복 검사를 수행할 수 있다.")
    @Test
    void checkNameDupl() {
        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.VERIFIED);

        String name = registerAcademy.getName();
        String phone = null;
        String email = null;

        // when
        boolean isOk = refreshAnd(() -> sut.checkDuplication(name, phone, email));

        // then
        assertThat(isOk).isFalse();
    }

    @DisplayName("학원의 연락처 중복 검사를 수행할 수 있다.")
    @Test
    void checkPhoneDupl() {
        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.VERIFIED);

        String name = null;
        String phone = registerAcademy.getPhone();
        String email = null;

        // when
        boolean isOk = refreshAnd(() -> sut.checkDuplication(name, phone, email));

        // then
        assertThat(isOk).isFalse();
    }

    @DisplayName("학원의 이메일 중복 검사를 수행할 수 있다.")
    @Test
    void checkEmailDupl() {
        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.VERIFIED);

        String name = null;
        String phone = null;
        String email = registerAcademy.getEmail();

        // when
        boolean isOk = refreshAnd(() -> sut.checkDuplication(name, phone, email));

        // then
        assertThat(isOk).isFalse();
    }

    @DisplayName("PENDING 상태의 학원은 중복 검사에서 제외된다.")
    @Test
    void ifPendingOk() {
        // given
        Academy academy = registerAcademy(AcademyStatus.PENDING);

        String name = academy.getName();
        String email = null;
        String phone = null;

        // when
        boolean isOk = refreshAnd(() -> sut.checkDuplication(name, phone, email));

        // then
        assertThat(isOk).isTrue();
    }

    @DisplayName("선생님이 자기 학원의 정보를 조회할 수 있다.")
    @Test
    public void getDetailByTeacher() {
        // given
        Academy academy = registerAcademy(AcademyStatus.VERIFIED);

        List<Teacher> saveTeachers = new ArrayList<>();
        saveTeachers.add(registerTeacher("Alice", UserStatus.ACTIVE, academy));
        saveTeachers.add(registerTeacher("Bob", UserStatus.ACTIVE, academy));
        saveTeachers.add(registerTeacher("Charlie", UserStatus.TRIAL, academy));
        saveTeachers.add(registerTeacher("David", UserStatus.TRIAL, academy));
        saveTeachers.add(registerTeacher("Eve", UserStatus.PENDING, academy));
        saveTeachers.add(registerTeacher("Frank", UserStatus.PENDING, academy));
        saveTeachers.add(registerTeacher("Grace", UserStatus.WITHDRAWN, academy));
        saveTeachers.add(registerTeacher("Hank", UserStatus.WITHDRAWN, academy));
        saveTeachers.add(registerTeacher("Ivy", UserStatus.FORBIDDEN, academy));
        saveTeachers.add(registerTeacher("Jack", UserStatus.FORBIDDEN, academy));
        saveTeachers.add(registerTeacher("Kate", UserStatus.TRIAL_END, academy));
        saveTeachers.add(registerTeacher("Liam", UserStatus.TRIAL_END, academy));
        for (Teacher t : saveTeachers) {
            createRole(t, ROLE_TEACHER);
        }

        Teacher director = registerTeacher("Director", UserStatus.ACTIVE, academy);
        createRole(director, ROLE_DIRECTOR, ROLE_TEACHER);
        saveTeachers.add(0, director);

        // when
        Map<String, Object> result = refreshAnd(() -> sut.getDetail(director.getUuid(), "teacher"));

        // then
        AcademyFullResponse academyFullResponse = (AcademyFullResponse) result.get("academy");
        assertThat(academyFullResponse).isNotNull();
        assertThat(academyFullResponse.getId()).isEqualTo(academy.getUuid());
        assertThat(academyFullResponse.getName()).isEqualTo(academy.getName());
        assertThat(academyFullResponse.getPhone()).isEqualTo(academy.getPhone());
        assertThat(academyFullResponse.getEmail()).isEqualTo(academy.getEmail());
        assertThat(academyFullResponse.isOpenToPublic()).isEqualTo(academy.isOpenToPublic());
        assertThat(academyFullResponse.getStatus()).isEqualTo(academy.getStatus());
        assertThat(academyFullResponse.getAddress().getStreet()).isEqualTo(academy.getAddress().getStreet());
        assertThat(academyFullResponse.getAddress().getDetail()).isEqualTo(academy.getAddress().getDetail());
        assertThat(academyFullResponse.getAddress().getPostalCode()).isEqualTo(academy.getAddress().getPostalCode());
        assertThat(academyFullResponse.getCreatedDateTime()).isNotNull();
        assertThat(academyFullResponse.getModifiedDateTime()).isNotNull();

        List<TeacherFullResponse> teacherFullResponses = (List<TeacherFullResponse>) result.get("teachers");
        assertThat(teacherFullResponses).hasSize(13)
                .extracting(TeacherFullResponse::getName)
                .containsExactlyElementsOf(
                        saveTeachers.stream().map(Teacher::getName).toList()
                );
    }

    @DisplayName("학생이 자기 학원의 정보를 조회할 수 있다.")
    @Test
    public void getDetailByStudent() {
        // given
        Academy academy = registerAcademy(AcademyStatus.VERIFIED);

        List<Teacher> saveTeachers = new ArrayList<>();
        saveTeachers.add(registerTeacher("Alice", UserStatus.ACTIVE, academy));
        saveTeachers.add(registerTeacher("Bob", UserStatus.ACTIVE, academy));
        saveTeachers.add(registerTeacher("Charlie", UserStatus.TRIAL, academy));
        saveTeachers.add(registerTeacher("David", UserStatus.TRIAL, academy));
        saveTeachers.add(registerTeacher("Eve", UserStatus.PENDING, academy));
        saveTeachers.add(registerTeacher("Frank", UserStatus.PENDING, academy));
        saveTeachers.add(registerTeacher("Grace", UserStatus.WITHDRAWN, academy));
        saveTeachers.add(registerTeacher("Hank", UserStatus.WITHDRAWN, academy));
        saveTeachers.add(registerTeacher("Ivy", UserStatus.FORBIDDEN, academy));
        saveTeachers.add(registerTeacher("Jack", UserStatus.FORBIDDEN, academy));
        saveTeachers.add(registerTeacher("Kate", UserStatus.TRIAL_END, academy));
        saveTeachers.add(registerTeacher("Liam", UserStatus.TRIAL_END, academy));
        for (Teacher t : saveTeachers) {
            createRole(t, ROLE_TEACHER);
        }

        Teacher director = registerTeacher("Director", UserStatus.ACTIVE, academy);
        createRole(director, ROLE_DIRECTOR, ROLE_TEACHER);
        saveTeachers.add(0, director);

        Student student = registerStudent(UserStatus.ACTIVE, academy);
        createRole(student, ROLE_STUDENT);

        // when
        Map<String, Object> result = refreshAnd(() -> sut.getDetail(student.getUuid(), "teacher"));

        // then
        AcademyFullResponse academyFullResponse = (AcademyFullResponse) result.get("academy");
        assertThat(academyFullResponse).isNotNull();
        assertThat(academyFullResponse.getId()).isEqualTo(academy.getUuid());
        assertThat(academyFullResponse.getName()).isEqualTo(academy.getName());
        assertThat(academyFullResponse.getPhone()).isEqualTo(academy.getPhone());
        assertThat(academyFullResponse.getEmail()).isEqualTo(academy.getEmail());
        assertThat(academyFullResponse.isOpenToPublic()).isEqualTo(academy.isOpenToPublic());
        assertThat(academyFullResponse.getStatus()).isEqualTo(academy.getStatus());
        assertThat(academyFullResponse.getAddress().getStreet()).isEqualTo(academy.getAddress().getStreet());
        assertThat(academyFullResponse.getAddress().getDetail()).isEqualTo(academy.getAddress().getDetail());
        assertThat(academyFullResponse.getAddress().getPostalCode()).isEqualTo(academy.getAddress().getPostalCode());
        assertThat(academyFullResponse.getCreatedDateTime()).isNotNull();
        assertThat(academyFullResponse.getModifiedDateTime()).isNotNull();

        List<TeacherPublicResponse> teacherPublicResponses = (List<TeacherPublicResponse>) result.get("teachers");
        assertThat(teacherPublicResponses).hasSize(13)
                .extracting(TeacherPublicResponse::getName)
                .containsExactlyElementsOf(
                        saveTeachers.stream().map(Teacher::getName).toList()
                );
    }

    @DisplayName("공개된 학원의 목록에서 학원 1개를 조회할 수 있다.")
    @Test
    public void getOnePublicAcademy() {
        // given
        Academy registerAcademy = registerAcademy(true, AcademyStatus.VERIFIED);
        AcademyPublicSearchCond cond = AcademyPublicSearchCond.builder()
                .page(1)
                .size(10)
                .prop("name")
                .order("asc")
                .academyName(registerAcademy.getName())
                .build();

        // when
        Map<String, Object> result = refreshAnd(() -> sut.getPublicList(cond));

        // then
        List<AcademyFullResponse> academies = (List<AcademyFullResponse>) result.get("academies");
        assertThat(academies).hasSize(1)
                .extracting(AcademyFullResponse::getId)
                .containsOnly(registerAcademy.getUuid());

        PageInfo pageInfo = (PageInfo) result.get("pageInfo");
        assertThat(pageInfo).isNotNull();
        assertThat(pageInfo.getPage()).isEqualTo(1);
        assertThat(pageInfo.getSize()).isEqualTo(10);
        assertThat(pageInfo.getTotal()).isEqualTo(1);
        assertThat(pageInfo.getLastPage()).isEqualTo(1);
        assertThat(pageInfo.getStart()).isEqualTo(1);
        assertThat(pageInfo.getEnd()).isEqualTo(1);
        assertThat(pageInfo.hasPrev()).isFalse();
        assertThat(pageInfo.hasNext()).isFalse();
    }

    @DisplayName("공개된 학원의 목록에서 다음 페이지를 조회할 수 있다.")
    @Test
    public void getNextPublicAcademy() {
        // given
        for (int i = 0; i < 11; i++) {
            registerAcademy(true, AcademyStatus.VERIFIED);
        }
        registerAcademy(false, AcademyStatus.VERIFIED);

        AcademyPublicSearchCond cond = AcademyPublicSearchCond.builder()
                .page(2)
                .size(10)
                .prop("name")
                .order("asc")
                .build();

        // when
        Map<String, Object> result = refreshAnd(() -> sut.getPublicList(cond));

        // then
        List<AcademyFullResponse> academies = (List<AcademyFullResponse>) result.get("academies");
        assertThat(academies).hasSize(1);

        PageInfo pageInfo = (PageInfo) result.get("pageInfo");
        assertThat(pageInfo).isNotNull();
        assertThat(pageInfo.getPage()).isEqualTo(2);
        assertThat(pageInfo.getSize()).isEqualTo(10);
        assertThat(pageInfo.getTotal()).isEqualTo(11);
        assertThat(pageInfo.getLastPage()).isEqualTo(2);
        assertThat(pageInfo.getStart()).isEqualTo(1);
        assertThat(pageInfo.getEnd()).isEqualTo(2);
        assertThat(pageInfo.hasPrev()).isFalse();
        assertThat(pageInfo.hasNext()).isFalse();
    }
}
