package psam.portfolio.sunder.english.docs.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import psam.portfolio.sunder.english.docs.RestDocsEnvironment;
import psam.portfolio.sunder.english.domain.academy.model.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.model.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.model.request.LostLoginIdForm;
import psam.portfolio.sunder.english.domain.user.model.request.LostLoginPwForm;
import psam.portfolio.sunder.english.domain.user.model.request.UserLoginForm;
import psam.portfolio.sunder.english.domain.user.model.request.UserPATCHPassword;
import psam.portfolio.sunder.english.domain.user.service.UserCommandService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static psam.portfolio.sunder.english.domain.user.model.enumeration.RoleName.*;

public class UserDocsTest extends RestDocsEnvironment {

    @Autowired
    UserCommandService userCommandService;

    @DisplayName("user 의 loginId 중복체크를 할 수 있다.")
    @Test
    void checkLoginIdDupl() throws Exception {
        // given
        String loginId = "uid";

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/users/check-dupl")
                        .contentType(APPLICATION_JSON)
                        .param("loginId", loginId)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                queryParameters(
                                        parameterWithName("loginId").description("중복체크할 아이디")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.isOk").type(BOOLEAN).description("중복 검사 결과")
                                )
                        )
                );
    }

    @DisplayName("user 의 email 중복체크를 할 수 있다.")
    @Test
    void checkEmailDupl() throws Exception {
        // given
        String email = "example@sunder.edu";

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/users/check-dupl")
                        .contentType(APPLICATION_JSON)
                        .param("email", email)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                queryParameters(
                                        parameterWithName("email").description("중복체크할 이메일")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.isOk").type(BOOLEAN).description("중복 검사 결과")
                                )
                        )
                );
    }

    @DisplayName("user 의 phone 중복체크를 할 수 있다.")
    @Test
    void checkPhoneDupl() throws Exception {
        // given
        String phone = "010121345678";

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/users/check-dupl")
                        .contentType(APPLICATION_JSON)
                        .param("phone", phone)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                queryParameters(
                                        parameterWithName("phone").description("중복체크할 연락처")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.isOk").type(BOOLEAN).description("중복 검사 결과")
                                )
                        )
                );
    }

    @DisplayName("사용자가 로그인할 수 있다.")
    @Test
    void login() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        UserLoginForm loginForm = new UserLoginForm(director.getLoginId(), infoContainer.getAnyRawPassword());

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/users/login")
                        .contentType(APPLICATION_JSON)
                        .content(createJson(loginForm))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                requestFields(
                                        fieldWithPath("loginId").type(STRING).description("로그인 아이디"),
                                        fieldWithPath("loginPw").type(STRING).description("비밀번호")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.accessToken").type(STRING).description("발급된 액세스 토큰. Authorization 헤더에 추가하여 사용"),
                                        fieldWithPath("data.refreshToken").type(STRING).description("발급된 리프레시 토큰. 액세스 토큰 만료 시 재발급에 사용"),
                                        fieldWithPath("data.passwordChangeRequired").type(BOOLEAN).description("비밀번호 변경 주기인지 여부"),
                                        fieldWithPath("data.userId").type(STRING).description("사용자 아이디"),
                                        fieldWithPath("data.academyId").type(STRING).description("(학원 소속인 경우)학원 아이디"),
                                        fieldWithPath("data.roleNames").type(ARRAY).description("사용자가 가진 권한 목록")
                                )
                        )
                );
    }

    @DisplayName("비밀번호를 바꾸지 않더라도 비밀번호 변경 알림을 3개월 지연할 수 있다.")
    @Test
    void alertPasswordChangeLater() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
        director.setLastPasswordChangeDateTime(LocalDateTime.now().minusMonths(4));

        String token = createBearerToken(director);

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/users/{userId}/password/alert-later", director.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, token)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                pathParameters(
                                        parameterWithName("userId").description("수정할 사용자 아이디")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.delay").type(BOOLEAN).description("비밀번호 변경 알림 지연 성공 여부")
                                )
                        )
                );
    }

    @DisplayName("로그인한 사용자가 토큰을 재발급할 수 있다.")
    @Test
    void refreshToken() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        String token = createBearerToken(director);

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/users/token/refresh")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, token)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                relaxedResponseFields(
                                        fieldWithPath("data.accessToken").type(STRING).description("새로 발급한 액세스 토큰. Authorization 헤더에 추가하여 사용")
                                )
                        )
                );
    }

    @DisplayName("개인정보를 입력하여 일치하는 로그인 아이디가 무엇인지 이메일로 받을 수 있다.")
    @Test
    void findLoginId() throws Exception {
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        LostLoginIdForm lostIdForm = new LostLoginIdForm(director.getEmail(), director.getName());

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/users/login-id/find")
                        .contentType(APPLICATION_JSON)
                        .content(createJson(lostIdForm))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                requestFields(
                                        fieldWithPath("email").type(STRING).description("조회할 가입한 사용자의 이메일"),
                                        fieldWithPath("name").type(STRING).description("조회할 가입한 사용자의 이름")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.emailSent").type(BOOLEAN).description("이메일 발송 여부. 해당하는 사용자가 있다면 true, 없다면 false")
                                )
                        )
                );
    }

    @DisplayName("개인정보를 입력하고 새로운 비밀번호를 생성하고 이메일로 받을 수 있다.")
    @Test
    void issueNewPassword() throws Exception {
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        LostLoginPwForm lostPwForm = new LostLoginPwForm(director.getLoginId(), director.getEmail(), director.getName());

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/users/password/new")
                        .contentType(APPLICATION_JSON)
                        .content(createJson(lostPwForm))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                requestFields(
                                        fieldWithPath("loginId").type(STRING).description("조회할 가입한 사용자의 로그인 아이디"),
                                        fieldWithPath("email").type(STRING).description("조회할 가입한 사용자의 이메일"),
                                        fieldWithPath("name").type(STRING).description("조회할 가입한 사용자의 이름")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.emailSent").type(BOOLEAN).description("이메일 발송 여부. 해당하는 사용자가 있다면 true, 없다면 false")
                                )
                        )
                );
    }

    @DisplayName("비밀번호를 한 번 더 입력하고 비밀번호 변경을 인가하는 토큰을 받을 수 있다.")
    @Test
    void authenticateToChangePassword() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        String token = createBearerToken(director);
        UserPATCHPassword patch = new UserPATCHPassword(infoContainer.getAnyRawPassword());

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/users/{userId}/password/change-auth", director.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, token)
                        .content(createJson(patch))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                pathParameters(
                                        parameterWithName("userId").description("수정할 사용자 아이디")
                                ),
                                requestFields(
                                        fieldWithPath("loginPw").type(STRING).description("기존 비밀번호")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.passwordChangeAllowedAmount").type(NUMBER).description("비밀번호 변경 유효 시간")
                                )
                        )
                );
    }

    @DisplayName("비밀번호로 다시 인증한 이후 비밀번호를 변경할 수 있다.")
    @Test
    void changePassword() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        userCommandService.authenticateToChangePassword(director.getId(), infoContainer.getAnyRawPassword());
        UserPATCHPassword patchPassword = new UserPATCHPassword(infoContainer.getAnyRawPassword());

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/users/{userId}/password/change-new", director.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(director))
                        .content(createJson(patchPassword))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                pathParameters(
                                        parameterWithName("userId").description("수정할 사용자 아이디")
                                ),
                                requestFields(
                                        fieldWithPath("loginPw").type(STRING).description("변경을 위해 새로 입력한 비밀번호")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.newPassword").type(BOOLEAN).description("비밀번호 변경 성공 여부")
                                )
                        )
                );
    }

    @DisplayName("선생님이 자기 자신의 정보를 조회할 수 있다.")
    @Test
    void getMyInfoByTeacher() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/users/me")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(director))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                relaxedResponseFields(
                                        fieldWithPath("data.id").type(STRING).description("사용자 아이디"),
                                        fieldWithPath("data.loginId").type(STRING).description("로그인 아이디"),
                                        fieldWithPath("data.name").type(STRING).description("이름"),
                                        fieldWithPath("data.email").type(STRING).description("이메일"),
                                        fieldWithPath("data.emailVerified").type(BOOLEAN).description("이메일 인증 여부"),
                                        fieldWithPath("data.phone").type(STRING).description("연락처"),
                                        fieldWithPath("data.street").type(STRING).description("주소"),
                                        fieldWithPath("data.addressDetail").type(STRING).description("상세 주소"),
                                        fieldWithPath("data.postalCode").type(STRING).description("우편번호"),
                                        fieldWithPath("data.status").type(STRING).description("사용자 상태"),
                                        fieldWithPath("data.roles").type(ARRAY).description("사용자 권한"),
                                        fieldWithPath("data.lastPasswordChangeDateTime").type(STRING).description("마지막 비밀번호 변경 일시"),
                                        fieldWithPath("data.academyId").type(STRING).description("소속 학원 아이디"),
                                        fieldWithPath("data.createdDateTime").type(STRING).description("생성 일시"),
                                        fieldWithPath("data.modifiedDateTime").type(STRING).description("수정 일시"),
                                        fieldWithPath("data.createdBy").type(STRING).description("생성자 아이디").optional(),
                                        fieldWithPath("data.modifiedBy").type(STRING).description("수정자 아이디").optional()
                                )
                        )
                );
    }

    @DisplayName("학생이 자기 자신의 정보를 조회할 수 있다.")
    @Test
    void getMyInfoByStudent() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, ROLE_STUDENT);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/users/me")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(student))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                relaxedResponseFields(
                                        fieldWithPath("data.id").type(STRING).description("사용자 아이디"),
                                        fieldWithPath("data.loginId").type(STRING).description("로그인 아이디"),
                                        fieldWithPath("data.name").type(STRING).description("이름"),
                                        fieldWithPath("data.email").type(STRING).description("이메일"),
                                        fieldWithPath("data.emailVerified").type(BOOLEAN).description("이메일 인증 여부"),
                                        fieldWithPath("data.phone").type(STRING).description("연락처"),
                                        fieldWithPath("data.street").type(STRING).description("주소"),
                                        fieldWithPath("data.addressDetail").type(STRING).description("상세 주소"),
                                        fieldWithPath("data.postalCode").type(STRING).description("우편번호"),
                                        fieldWithPath("data.status").type(STRING).description("사용자 상태"),
                                        fieldWithPath("data.roles").type(ARRAY).description("사용자 권한"),
                                        fieldWithPath("data.lastPasswordChangeDateTime").type(STRING).description("마지막 비밀번호 변경 일시"),
                                        fieldWithPath("data.attendanceId").type(STRING).description("출석 아이디"),
                                        fieldWithPath("data.schoolName").type(STRING).description("학교 이름"),
                                        fieldWithPath("data.schoolGrade").type(NUMBER).description("학년"),
                                        fieldWithPath("data.parentName").type(STRING).description("부모 이름"),
                                        fieldWithPath("data.parentPhone").type(STRING).description("부모 연락처"),
                                        fieldWithPath("data.academyId").type(STRING).description("소속 학원 아이디"),
                                        fieldWithPath("data.createdDateTime").type(STRING).description("생성 일시"),
                                        fieldWithPath("data.modifiedDateTime").type(STRING).description("수정 일시"),
                                        fieldWithPath("data.createdBy").type(STRING).description("생성자 아이디").optional(),
                                        fieldWithPath("data.modifiedBy").type(STRING).description("수정자 아이디").optional()
                                )
                        )
                );
    }
}
