package psam.portfolio.sunder.english.docs.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import psam.portfolio.sunder.english.docs.RestDocsEnvironment;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.request.AcademyDirectorPOST;
import psam.portfolio.sunder.english.domain.academy.model.request.AcademyPATCH;
import psam.portfolio.sunder.english.domain.academy.service.AcademyCommandService;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.domain.user.model.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.model.request.UserLoginForm;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

public class AcademyDocsTest extends RestDocsEnvironment {

    @Autowired
    AcademyCommandService academyCommandService;

    @Autowired
    TeacherQueryRepository teacherQueryRepository;

    @DisplayName("academy 의 name 중복체크를 할 수 있다.")
    @Test
    void checkNameDupl() throws Exception {
        // given
        String name = infoContainer.getUniqueAcademyName();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/academies/check-dupl")
                        .contentType(APPLICATION_JSON)
                        .param("name", name)
                        .param("academyId", UUID.randomUUID().toString())
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                queryParameters(
                                        parameterWithName("name").description("중복체크할 학원 이름"),
                                        parameterWithName("academyId").description("중복체크에서 제외할 학원 아이디")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.isOk").type(BOOLEAN).description("중복 검사 결과")
                                )
                        )
                );
    }

    @DisplayName("academy 의 phone 중복체크를 할 수 있다.")
    @Test
    void checkPhoneDupl() throws Exception {
        // given
        String phone = infoContainer.getUniquePhoneNumber();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/academies/check-dupl")
                        .contentType(APPLICATION_JSON)
                        .param("phone", phone)
                        .param("academyId", UUID.randomUUID().toString())
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                queryParameters(
                                        parameterWithName("phone").description("중복체크할 전화번호"),
                                        parameterWithName("academyId").description("중복체크에서 제외할 학원 아이디")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.isOk").type(BOOLEAN).description("중복 검사 결과")
                                )
                        )
                );
    }

    @DisplayName("academy 의 email 중복체크를 할 수 있다.")
    @Test
    void checkEmailDupl() throws Exception {
        // given
        String email = infoContainer.getUniqueEmail();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/academies/check-dupl")
                        .contentType(APPLICATION_JSON)
                        .param("email", email)
                        .param("academyId", UUID.randomUUID().toString())
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                queryParameters(
                                        parameterWithName("email").description("중복체크할 이메일"),
                                        parameterWithName("academyId").description("중복체크에서 제외할 학원 아이디")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.isOk").type(BOOLEAN).description("중복 검사 결과")
                                )
                        )
                );
    }

    @DisplayName("academy 와 director 를 등록할 수 있다.")
    @Test
    void registerAcademy() throws Exception {
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        Address anyAddress = infoContainer.getAnyAddress();

        AcademyDirectorPOST.AcademyPOST buildAcademyPOST = AcademyDirectorPOST.AcademyPOST.builder()
                .name(infoContainer.getUniqueAcademyName())
                .phone(infoContainer.getUniquePhoneNumber())
                .email(infoContainer.getUniqueEmail())
                .street(anyAddress.getStreet())
                .addressDetail(anyAddress.getDetail())
                .postalCode(anyAddress.getPostalCode())
                .openToPublic(true)
                .build();

        AcademyDirectorPOST.DirectorPOST buildDirectorPOST = AcademyDirectorPOST.DirectorPOST.builder()
                .loginId(infoContainer.getUniqueLoginId())
                .loginPw("P@ssw0rd")
                .name("홍길동")
                .email(infoContainer.getUniqueEmail())
                .phone(infoContainer.getUniquePhoneNumber())
                .street(anyAddress.getStreet())
                .addressDetail(anyAddress.getDetail())
                .postalCode(anyAddress.getPostalCode())
                .build();

        AcademyDirectorPOST post = new AcademyDirectorPOST(buildAcademyPOST, buildDirectorPOST);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/academies")
                        .contentType(APPLICATION_JSON)
                        .content(createJson(post))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("academy.name").type(STRING).description("학원 이름"),
                                fieldWithPath("academy.phone").type(STRING).description("학원 전화번호").optional(),
                                fieldWithPath("academy.email").type(STRING).description("학원 이메일").optional(),
                                fieldWithPath("academy.street").type(STRING).description("학원 주소").optional(),
                                fieldWithPath("academy.addressDetail").type(STRING).description("학원 상세주소").optional(),
                                fieldWithPath("academy.postalCode").type(STRING).description("학원 우편번호").optional(),
                                fieldWithPath("academy.openToPublic").type(BOOLEAN).description("학원 공개 여부"),
                                fieldWithPath("director.loginId").type(STRING).description("학원장 로그인 아이디"),
                                fieldWithPath("director.loginPw").type(STRING).description("학원장 로그인 비밀번호"),
                                fieldWithPath("director.name").type(STRING).description("학원장 이름"),
                                fieldWithPath("director.email").type(STRING).description("학원장 이메일"),
                                fieldWithPath("director.phone").type(STRING).description("학원장 전화번호").optional(),
                                fieldWithPath("director.street").type(STRING).description("학원장 주소").optional(),
                                fieldWithPath("director.addressDetail").type(STRING).description("학원장 상세주소").optional(),
                                fieldWithPath("director.postalCode").type(STRING).description("학원장 우편번호").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.directorId").type(STRING).description("등록된 학원장의 아이디")
                        )
                ));
    }

    @DisplayName("academy 의 uuid 를 검증하고 승인할 수 있다.")
    @Test
    void verifyAcademy() throws Exception {
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        Address anyAddress = infoContainer.getAnyAddress();

        AcademyDirectorPOST.AcademyPOST buildAcademyPOST = AcademyDirectorPOST.AcademyPOST.builder()
                .name(infoContainer.getUniqueAcademyName())
                .phone(infoContainer.getUniquePhoneNumber())
                .email(infoContainer.getUniqueEmail())
                .street(anyAddress.getStreet())
                .addressDetail(anyAddress.getDetail())
                .postalCode(anyAddress.getPostalCode())
                .openToPublic(true)
                .build();

        AcademyDirectorPOST.DirectorPOST buildDirectorPOST = AcademyDirectorPOST.DirectorPOST.builder()
                .loginId(infoContainer.getUniqueLoginId())
                .loginPw("P@ssw0rd")
                .name("홍길동")
                .email(infoContainer.getUniqueEmail())
                .phone(infoContainer.getUniquePhoneNumber())
                .street(anyAddress.getStreet())
                .addressDetail(anyAddress.getDetail())
                .postalCode(anyAddress.getPostalCode())
                .build();

        UUID directorId = refreshAnd(() -> academyCommandService.registerDirectorWithAcademy(buildAcademyPOST, buildDirectorPOST));
        Teacher getDirector = teacherQueryRepository.getById(directorId);
        UUID academyId = getDirector.getAcademy().getId();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/academies/{academyId}/verify", academyId.toString())
                        .contentType(APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("academyId").description("학원 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.verified").type(BOOLEAN).description("학원 승인 여부")
                        )
                ));
    }

    @DisplayName("선생님이 자기 학원의 상세 정보를 조회할 수 있다.")
    @Test
    void getDetailByTeacher() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);
        String token = createBearerToken(teacher);

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/academies/{academyId}", academy.getId().toString())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, token)
                        .param("select", "teacher")
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                pathParameters(
                                        parameterWithName("academyId").description("조회할 학원 아이디")
                                ),
                                queryParameters(
                                        parameterWithName("select").description("""
                                                같이 조회할 정보 옵션 +
                                                - teacher : 학원에 소속된 선생님 목록을 같이 조회
                                                """).optional()
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.academy.id").type(STRING).description("학원 아이디"),
                                        fieldWithPath("data.academy.name").type(STRING).description("학원 이름"),
                                        fieldWithPath("data.academy.street").type(STRING).description("학원 주소 (도로명)"),
                                        fieldWithPath("data.academy.addressDetail").type(STRING).description("학원 주소 (상세주소)"),
                                        fieldWithPath("data.academy.postalCode").type(STRING).description("학원 주소 (우편번호)"),
                                        fieldWithPath("data.academy.phone").type(STRING).description("학원 전화번호"),
                                        fieldWithPath("data.academy.email").type(STRING).description("학원 이메일"),
                                        fieldWithPath("data.academy.openToPublic").type(BOOLEAN).description("학원 공개 여부"),
                                        fieldWithPath("data.academy.status").type(STRING).description("학원 상태"),
                                        fieldWithPath("data.academy.createdDateTime").type(STRING).description("학원 생성일시"),
                                        fieldWithPath("data.academy.modifiedDateTime").type(STRING).description("학원 수정일시"),
                                        fieldWithPath("data.teachers[].id").type(STRING).description("선생님 아이디"),
                                        fieldWithPath("data.teachers[].loginId").type(STRING).description("선생님 로그인 아이디"),
                                        fieldWithPath("data.teachers[].name").type(STRING).description("선생님 이름"),
                                        fieldWithPath("data.teachers[].email").type(STRING).description("선생님 이메일"),
                                        fieldWithPath("data.teachers[].phone").type(STRING).description("선생님 전화번호"),
                                        fieldWithPath("data.teachers[].street").type(STRING).description("선생님 주소 (도로명)"),
                                        fieldWithPath("data.teachers[].addressDetail").type(STRING).description("선생님 주소 (상세주소)"),
                                        fieldWithPath("data.teachers[].postalCode").type(STRING).description("선생님 주소 (우편번호)"),
                                        fieldWithPath("data.teachers[].status").type(STRING).description("선생님 상태"),
                                        fieldWithPath("data.teachers[].roles[]").type(ARRAY).description("선생님 권한"),
                                        fieldWithPath("data.teachers[].lastPasswordChangeDateTime").type(STRING).description("선생님 마지막 비밀번호 변경일시"),
                                        fieldWithPath("data.teachers[].academyId").type(STRING).description("선생님이 속한 학원 아이디"),
                                        fieldWithPath("data.teachers[].createdDateTime").type(STRING).description("선생님 생성일시"),
                                        fieldWithPath("data.teachers[].modifiedDateTime").type(STRING).description("선생님 수정일시"),
                                        fieldWithPath("data.teachers[].createdBy").type(STRING).description("생성자 아이디").optional(),
                                        fieldWithPath("data.teachers[].modifiedBy").type(STRING).description("수정자 아이디").optional()
                                )
                        )
                );
    }

    @DisplayName("학생이 자기 학원의 상세 정보를 조회할 수 있다.")
    @Test
    void getDetailByStudent() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);

        Teacher director = dataCreator.registerTeacher("Director", UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
        Teacher teacher = dataCreator.registerTeacher("Alice", UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, ROLE_STUDENT);

        String token = createBearerToken(student);

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/academies/{academyId}", academy.getId().toString())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, token)
                        .param("select", "teacher")
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                pathParameters(
                                        parameterWithName("academyId").description("조회할 학원 아이디")
                                ),
                                queryParameters(
                                        parameterWithName("select").description("""
                                                같이 조회할 정보 옵션 +
                                                - teacher : 학원에 소속된 선생님 목록을 같이 조회
                                                """).optional()
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.academy.id").type(STRING).description("학원 아이디"),
                                        fieldWithPath("data.academy.name").type(STRING).description("학원 이름"),
                                        fieldWithPath("data.academy.street").type(STRING).description("학원 주소 (도로명)"),
                                        fieldWithPath("data.academy.addressDetail").type(STRING).description("학원 주소 (상세주소)"),
                                        fieldWithPath("data.academy.postalCode").type(STRING).description("학원 주소 (우편번호)"),
                                        fieldWithPath("data.academy.phone").type(STRING).description("학원 전화번호"),
                                        fieldWithPath("data.academy.email").type(STRING).description("학원 이메일"),
                                        fieldWithPath("data.academy.openToPublic").type(BOOLEAN).description("학원 공개 여부"),
                                        fieldWithPath("data.academy.status").type(STRING).description("학원 상태"),
                                        fieldWithPath("data.academy.createdDateTime").type(STRING).description("학원 생성일시"),
                                        fieldWithPath("data.academy.modifiedDateTime").type(STRING).description("학원 수정일시"),
                                        fieldWithPath("data.teachers[].id").type(STRING).description("선생님 아이디"),
                                        fieldWithPath("data.teachers[].name").type(STRING).description("선생님 이름"),
                                        fieldWithPath("data.teachers[].status").type(STRING).description("선생님 상태"),
                                        fieldWithPath("data.teachers[].roles[]").type(ARRAY).description("선생님 권한"),
                                        fieldWithPath("data.teachers[].createdDateTime").type(STRING).description("선생님 생성일시"),
                                        fieldWithPath("data.teachers[].modifiedDateTime").type(STRING).description("선생님 수정일시")
                                )
                        )
                );
    }

    @DisplayName("academy 의 정보를 수정할 수 있다.")
    @Test
    void updateInfo() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(true, AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
        String token = createBearerToken(director);

        refresh();

        AcademyPATCH academyPATCH = AcademyPATCH.builder()
                .name("수정된학원이름")
                .phone("01012345678")
                .street("수정된 학원 주소")
                .addressDetail("수정된 학원 상세주소")
                .postalCode("12345")
                .openToPublic(false)
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/academies/{academyId}", academy.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, token)
                        .content(createJson(academyPATCH))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                pathParameters(
                                        parameterWithName("academyId").description("수정할 학원 아이디")
                                ),
                                requestFields(
                                        fieldWithPath("name").type(STRING).description("학원 이름"),
                                        fieldWithPath("phone").type(STRING).description("학원 전화번호").optional(),
                                        fieldWithPath("street").type(STRING).description("학원 주소").optional(),
                                        fieldWithPath("addressDetail").type(STRING).description("학원 상세주소").optional(),
                                        fieldWithPath("postalCode").type(STRING).description("학원 우편번호").optional(),
                                        fieldWithPath("openToPublic").type(BOOLEAN).description("학원 공개 여부")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.academyId").type(STRING).description("수정을 완료한 학원 아이디")
                                )
                        )
                );
    }

    @DisplayName("공개된 학원 목록을 조회할 수 있다.")
    @Test
    void getPublicList() throws Exception {
        // given
        for (int i = 0; i < 3; i++) {
            dataCreator.registerAcademy(true, AcademyStatus.VERIFIED);
        }

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/academies")
                        .contentType(APPLICATION_JSON)
                        .param("page", "1")
                        .param("size", "10")
                        .param("prop", "name")
                        .param("dir", "asc")
                        .param("academyName", "학원")
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                                queryParameters(
                                        parameterWithName("page").description("페이지 번호. 최소 1"),
                                        parameterWithName("size").description("페이지 크기. 최소 10"),
                                        parameterWithName("prop").description("""
                                                정렬 기준 +
                                                - name : 학원 이름 +
                                                - (default) 생성 순서
                                                """).optional(),
                                        parameterWithName("dir").description("""
                                                정렬 방향 +
                                                - asc : 오름차순 +
                                                - desc : 내림차순(기본값)
                                                """).optional(),
                                        parameterWithName("academyName").description("검색할 학원 이름").optional()
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.academies[].id").type(STRING).description("학원 아이디"),
                                        fieldWithPath("data.academies[].name").type(STRING).description("학원 이름"),
                                        fieldWithPath("data.academies[].street").type(STRING).description("학원 주소 (도로명)"),
                                        fieldWithPath("data.academies[].addressDetail").type(STRING).description("학원 주소 (상세주소)"),
                                        fieldWithPath("data.academies[].postalCode").type(STRING).description("학원 주소 (우편번호)"),
                                        fieldWithPath("data.academies[].phone").type(STRING).description("학원 전화번호"),
                                        fieldWithPath("data.academies[].email").type(STRING).description("학원 이메일"),
                                        fieldWithPath("data.academies[].openToPublic").type(BOOLEAN).description("학원 공개 여부"),
                                        fieldWithPath("data.academies[].status").type(STRING).description("학원 상태"),
                                        fieldWithPath("data.academies[].createdDateTime").type(STRING).description("학원 생성일시"),
                                        fieldWithPath("data.academies[].modifiedDateTime").type(STRING).description("학원 수정일시"),
                                        fieldWithPath("data.pageInfo.page").type(NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("data.pageInfo.size").type(NUMBER).description("페이지 크기"),
                                        fieldWithPath("data.pageInfo.total").type(NUMBER).description("전체 학원 수"),
                                        fieldWithPath("data.pageInfo.lastPage").type(NUMBER).description("마지막 페이지 번호"),
                                        fieldWithPath("data.pageInfo.start").type(NUMBER).description("페이지 세트의 시작 번호"),
                                        fieldWithPath("data.pageInfo.end").type(NUMBER).description("페이지 세트의 끝 번호"),
                                        fieldWithPath("data.pageInfo.hasPrev").type(BOOLEAN).description("이전 페이지 존재 여부"),
                                        fieldWithPath("data.pageInfo.hasNext").type(BOOLEAN).description("다음 페이지 존재 여부")
                                )
                        )
                );
    }


    @DisplayName("학원장은 자기 학원을 폐쇄 신청할 수 있다.")
    @Test
    void withdraw() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(true, AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
        String token = createBearerToken(director);

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/academies/{academyId}", academy.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, token)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("academyId").description("폐쇄할 학원 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.academyId").type(STRING).description("페쇄를 신청한 학원 아이디")
                        )
                ));
    }

    @DisplayName("학원장은 자기 학원의 폐쇄 신청을 취소할 수 있다.")
    @Test
    void revokeWithdrawal() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(true, AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        refreshAnd(() -> academyCommandService.withdraw(director.getId()));

        String token = createBearerToken(director);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/academies/{academyId}/revoke", academy.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, token)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("academyId").description("수정할 학원 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.academyId").type(STRING).description("페쇄를 취소한 학원 아이디")
                        )
                ));
    }

    @DisplayName("사용 체험 중인 학원장이 정규회원으로 전환할 수 있다.")
    @Test
    void endTrial() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.TRIAL, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
        String token = createBearerToken(director);

        UserLoginForm loginForm = new UserLoginForm(director.getLoginId(), infoContainer.getAnyRawPassword());

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/academies/end-trial")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, token)
                        .content(createJson(loginForm))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("loginId").type(STRING).description("학원장 로그인 아이디"),
                                fieldWithPath("loginPw").type(STRING).description("학원장 로그인 비밀번호")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.endTrial").type(BOOLEAN).description("정규전환 성공 여부")
                        )
                ));
    }
}
