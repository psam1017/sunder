package psam.portfolio.sunder.english.docs.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import psam.portfolio.sunder.english.docs.RestDocsEnvironment;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPATCHInfo;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPATCHStatus;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPOST;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPOSTRoles;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;

import java.util.Set;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.*;

public class TeacherDocsTest extends RestDocsEnvironment {

    @DisplayName("선생님이 선생님을 등록할 수 있다.")
    @Test
    void registerTeacher() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        TeacherPOST post = TeacherPOST.builder()
                .loginId(infoContainer.getUniqueLoginId())
                .loginPw("P@ssw0rd")
                .name("홍길동")
                .email(infoContainer.getUniqueEmail())
                .phone(infoContainer.getUniquePhoneNumber())
                .street(infoContainer.getAnyAddress().getStreet())
                .addressDetail(infoContainer.getAnyAddress().getDetail())
                .postalCode(infoContainer.getAnyAddress().getPostalCode())
                .build();

        String token = createBearerToken(director);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/teachers")
                        .header(AUTHORIZATION, token)
                        .contentType(APPLICATION_JSON)
                        .content(createJson(post))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("loginId").type(STRING).description("선생님 로그인 아이디"),
                                fieldWithPath("loginPw").type(STRING).description("선생님 로그인 비밀번호"),
                                fieldWithPath("name").type(STRING).description("선생님 이름"),
                                fieldWithPath("email").type(STRING).description("선생님 이메일"),
                                fieldWithPath("phone").type(STRING).description("선생님 전화번호").optional(),
                                fieldWithPath("street").type(STRING).description("선생님 주소").optional(),
                                fieldWithPath("addressDetail").type(STRING).description("선생님 상세주소").optional(),
                                fieldWithPath("postalCode").type(STRING).description("선생님 우편번호").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.teacherId").type(STRING).description("등록된 선생님의 아이디")
                        )
                ));
    }

    @DisplayName("선생님이 선생님 목록을 조회할 수 있다.")
    @Test
    void getTeacherListByTeacher() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        Teacher teacher1 = dataCreator.registerTeacher("Alice", UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher1, ROLE_TEACHER);
        Teacher teacher2 = dataCreator.registerTeacher("Abigail", UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher2, ROLE_TEACHER);
        Teacher teacher3 = dataCreator.registerTeacher("Adam", UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher3, ROLE_TEACHER);

        String token = createBearerToken(director);

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/teachers")
                        .header(AUTHORIZATION, token)
                        .contentType(APPLICATION_JSON)
                        .param("prop", "name")
                        .param("dir", "asc")
                        .param("status", "ACTIVE")
                        .param("teacherName", "A")
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("prop").description("""
                                        정렬 기준 +
                                        - name: 이름 +
                                        - status : 상태 +
                                        - (default) 생성 순서
                                        """).optional(),
                                parameterWithName("dir").description("""
                                        정렬 방향 +
                                        - asc : 오름차순 +
                                        - desc : 내림차순(기본값)
                                        """).optional(),
                                parameterWithName("status").description("선생님 상태").optional(),
                                parameterWithName("teacherName").description("선생님 이름").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.teachers[].id").type(STRING).description("선생님 아이디"),
                                fieldWithPath("data.teachers[].loginId").type(STRING).description("선생님 로그인 아이디"),
                                fieldWithPath("data.teachers[].name").type(STRING).description("선생님 이름"),
                                fieldWithPath("data.teachers[].email").type(STRING).description("선생님 이메일"),
                                fieldWithPath("data.teachers[].emailVerified").type(BOOLEAN).description("이메일 인증 여부"),
                                fieldWithPath("data.teachers[].phone").type(STRING).description("선생님 전화번호").optional(),
                                fieldWithPath("data.teachers[].street").type(STRING).description("주소"),
                                fieldWithPath("data.teachers[].addressDetail").type(STRING).description("상세주소"),
                                fieldWithPath("data.teachers[].postalCode").type(STRING).description("우편번호"),
                                fieldWithPath("data.teachers[].status").type(STRING).description("선생님 상태"),
                                fieldWithPath("data.teachers[].roles").type(ARRAY).description("선생님 권한"),
                                fieldWithPath("data.teachers[].lastPasswordChangeDateTime").type(STRING).description("마지막 비밀번호 변경 일시"),
                                fieldWithPath("data.teachers[].academyId").type(STRING).description("학원 아이디"),
                                fieldWithPath("data.teachers[].createdDateTime").type(STRING).description("생성 일시"),
                                fieldWithPath("data.teachers[].modifiedDateTime").type(STRING).description("수정 일시"),
                                fieldWithPath("data.teachers[].createdBy").type(STRING).description("생성자 아이디").optional(),
                                fieldWithPath("data.teachers[].modifiedBy").type(STRING).description("수정자 아이디").optional()
                        )
                ));
    }

    @DisplayName("학생이 선생님 목록을 조회할 수 있다.")
    @Test
    void getTeacherListByStudent() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        Teacher teacher1 = dataCreator.registerTeacher("Alice", UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher1, ROLE_TEACHER);
        Teacher teacher2 = dataCreator.registerTeacher("Abigail", UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher2, ROLE_TEACHER);
        Teacher teacher3 = dataCreator.registerTeacher("Adam", UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher3, ROLE_TEACHER);

        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, ROLE_STUDENT);

        String token = createBearerToken(student);

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/teachers")
                        .header(AUTHORIZATION, token)
                        .contentType(APPLICATION_JSON)
                        .param("prop", "name")
                        .param("dir", "asc")
                        .param("status", "ACTIVE")
                        .param("teacherName", "A")
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("prop").description("""
                                        정렬 기준 +
                                        - name: 이름 +
                                        - status : 상태 +
                                        - (default) 생성 순서
                                        """).optional(),
                                parameterWithName("dir").description("""
                                        정렬 방향 +
                                        - asc : 오름차순 +
                                        - desc : 내림차순(기본값)
                                        """).optional(),
                                parameterWithName("status").description("선생님 상태").optional(),
                                parameterWithName("teacherName").description("선생님 이름").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.teachers[].id").type(STRING).description("선생님 아이디"),
                                fieldWithPath("data.teachers[].name").type(STRING).description("선생님 이름"),
                                fieldWithPath("data.teachers[].status").type(STRING).description("선생님 상태"),
                                fieldWithPath("data.teachers[].roles").type(ARRAY).description("선생님 권한"),
                                fieldWithPath("data.teachers[].createdDateTime").type(STRING).description("생성 일시"),
                                fieldWithPath("data.teachers[].modifiedDateTime").type(STRING).description("수정 일시")
                        )
                ));
    }

    @DisplayName("선생님이 선생님 상세 정보를 조회할 수 있다.")
    @Test
    void getTeacherDetailByTeacher() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);

        String token = createBearerToken(director);

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/teachers/{teacherId}", teacher.getId())
                        .header(AUTHORIZATION, token)
                        .contentType(APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("teacherId").description("선생님 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.id").type(STRING).description("선생님 아이디"),
                                fieldWithPath("data.loginId").type(STRING).description("선생님 로그인 아이디"),
                                fieldWithPath("data.name").type(STRING).description("선생님 이름"),
                                fieldWithPath("data.email").type(STRING).description("선생님 이메일"),
                                fieldWithPath("data.emailVerified").type(BOOLEAN).description("이메일 인증 여부"),
                                fieldWithPath("data.phone").type(STRING).description("선생님 전화번호").optional(),
                                fieldWithPath("data.street").type(STRING).description("주소"),
                                fieldWithPath("data.addressDetail").type(STRING).description("상세주소"),
                                fieldWithPath("data.postalCode").type(STRING).description("우편번호"),
                                fieldWithPath("data.status").type(STRING).description("선생님 상태"),
                                fieldWithPath("data.roles").type(ARRAY).description("선생님 권한"),
                                fieldWithPath("data.lastPasswordChangeDateTime").type(STRING).description("마지막 비밀번호 변경 일시"),
                                fieldWithPath("data.academyId").type(STRING).description("학원 아이디"),
                                fieldWithPath("data.createdDateTime").type(STRING).description("생성 일시"),
                                fieldWithPath("data.modifiedDateTime").type(STRING).description("수정 일시"),
                                fieldWithPath("data.createdBy").type(STRING).description("생성자 아이디").optional(),
                                fieldWithPath("data.modifiedBy").type(STRING).description("수정자 아이디").optional()
                        )
                ));
    }

    @DisplayName("학생이 선생님 상세 정보를 조회할 수 있다.")
    @Test
    void getTeacherDetailByStudent() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);

        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, ROLE_STUDENT);

        String token = createBearerToken(student);

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/teachers/{teacherId}", teacher.getId())
                        .header(AUTHORIZATION, token)
                        .contentType(APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("teacherId").description("선생님 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.id").type(STRING).description("선생님 아이디"),
                                fieldWithPath("data.name").type(STRING).description("선생님 이름"),
                                fieldWithPath("data.status").type(STRING).description("선생님 상태"),
                                fieldWithPath("data.roles").type(ARRAY).description("선생님 권한"),
                                fieldWithPath("data.createdDateTime").type(STRING).description("생성 일시"),
                                fieldWithPath("data.modifiedDateTime").type(STRING).description("수정 일시")
                        )
                ));
    }

    @DisplayName("학원장이 선생님 상태를 변경할 수 있다.")
    @Test
    void changeTeacherStatus() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);

        String token = createBearerToken(director);

        TeacherPATCHStatus patch = new TeacherPATCHStatus(UserStatus.WITHDRAWN);

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/api/teachers/{teacherId}/status", teacher.getId())
                        .header(AUTHORIZATION, token)
                        .contentType(APPLICATION_JSON)
                        .content(createJson(patch))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("teacherId").description("선생님 아이디")
                        ),
                        requestFields(
                                fieldWithPath("status").type(STRING).description("변경할 선생님 상태")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.teacherId").type(STRING).description("변경된 선생님 아이디"),
                                fieldWithPath("data.status").type(STRING).description("변경된 선생님 상태")
                        )
                ));
    }

    @DisplayName("학원장이 선생님 권한을 변경할 수 있다.")
    @Test
    void changeTeacherRoles() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);

        String token = createBearerToken(director);

        TeacherPOSTRoles put = new TeacherPOSTRoles(Set.of(ROLE_TEACHER, ROLE_DIRECTOR));

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/teachers/{teacherId}/roles", teacher.getId())
                        .header(AUTHORIZATION, token)
                        .contentType(APPLICATION_JSON)
                        .content(createJson(put))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("teacherId").description("선생님 아이디")
                        ),
                        requestFields(
                                fieldWithPath("roles").type(ARRAY).description("변경할 선생님 권한")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.teacherId").type(STRING).description("변경된 선생님 아이디"),
                                fieldWithPath("data.roles").type(ARRAY).description("변경된 선생님 권한")
                        )
                ));
    }

    @DisplayName("선생님이 자기 자신의 개인정보를 수정할 수 있다.")
    @Test
    void updateTeacherInfo() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);

        TeacherPATCHInfo patch = TeacherPATCHInfo.builder()
                .name("선더선생님")
                .phone(infoContainer.getUniquePhoneNumber())
                .email(infoContainer.getUniqueEmail())
                .street(infoContainer.getAnyAddress().getStreet())
                .addressDetail(infoContainer.getAnyAddress().getDetail())
                .postalCode(infoContainer.getAnyAddress().getPostalCode())
                .build();

        String token = createBearerToken(teacher);

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/api/teachers/personal-info")
                        .header(AUTHORIZATION, token)
                        .contentType(APPLICATION_JSON)
                        .content(createJson(patch))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("name").type(STRING).description("변경할 선생님 이름"),
                                fieldWithPath("phone").type(STRING).description("변경할 선생님 전화번호").optional(),
                                fieldWithPath("email").type(STRING).description("변경할 선생님 이메일").optional(),
                                fieldWithPath("street").type(STRING).description("변경할 선생님 주소").optional(),
                                fieldWithPath("addressDetail").type(STRING).description("변경할 선생님 상세주소").optional(),
                                fieldWithPath("postalCode").type(STRING).description("변경할 선생님 우편번호").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.teacherId").type(STRING).description("변경된 선생님 아이디")
                        )
                ));
    }
}
