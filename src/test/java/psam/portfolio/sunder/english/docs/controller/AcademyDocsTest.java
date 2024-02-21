package psam.portfolio.sunder.english.docs.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import psam.portfolio.sunder.english.docs.RestDocsEnvironment;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.web.teacher.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;
import psam.portfolio.sunder.english.web.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.web.teacher.model.request.AcademyDirectorPOST;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.web.teacher.service.AcademyCommandService;
import psam.portfolio.sunder.english.web.user.enumeration.RoleName;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.web.user.model.entity.User;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static psam.portfolio.sunder.english.web.user.enumeration.RoleName.*;

public class AcademyDocsTest extends RestDocsEnvironment {

    @Autowired
    AcademyCommandService academyCommandService;

    @Autowired
    TeacherQueryRepository teacherQueryRepository;

    @DisplayName("academy 의 name 중복체크를 할 수 있다.")
    @Test
    void checkNameDupl() throws Exception {
        // given
        String name = uic.getUniqueAcademyName();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/academy/check-dupl")
                        .contentType(APPLICATION_JSON)
                        .param("name", name)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                                queryParameters(
                                        parameterWithName("name").description("중복체크할 학원 이름")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.isOk")
                                                .type(BOOLEAN)
                                                .description("중복 검사 결과")
                                )
                        )
                );
    }

    @DisplayName("academy 의 phone 중복체크를 할 수 있다.")
    @Test
    void checkPhoneDupl() throws Exception {
        // given
        String phone = uic.getUniquePhoneNumber();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/academy/check-dupl")
                        .contentType(APPLICATION_JSON)
                        .param("phone", phone)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                                queryParameters(
                                        parameterWithName("phone").description("중복체크할 전화번호")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.isOk")
                                                .type(BOOLEAN)
                                                .description("중복 검사 결과")
                                )
                        )
                );
    }

    @DisplayName("academy 의 email 중복체크를 할 수 있다.")
    @Test
    void checkEmailDupl() throws Exception {
        // given
        String email = uic.getUniqueEmail();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/academy/check-dupl")
                        .contentType(APPLICATION_JSON)
                        .param("email", email)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                                queryParameters(
                                        parameterWithName("email").description("중복체크할 이메일")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.isOk")
                                                .type(BOOLEAN)
                                                .description("중복 검사 결과")
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
        Address anyAddress = uic.getAnyAddress();

        AcademyDirectorPOST.AcademyPOST buildAcademyPOST = AcademyDirectorPOST.AcademyPOST.builder()
                .name(uic.getUniqueAcademyName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street(anyAddress.getStreet())
                .addressDetail(anyAddress.getDetail())
                .postalCode(anyAddress.getPostalCode())
                .openToPublic(true)
                .build();

        AcademyDirectorPOST.DirectorPOST buildDirectorPOST = AcademyDirectorPOST.DirectorPOST.builder()
                .loginId(uic.getUniqueLoginId())
                .loginPw("P@ssw0rd")
                .name("홍길동")
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .street(anyAddress.getStreet())
                .addressDetail(anyAddress.getDetail())
                .postalCode(anyAddress.getPostalCode())
                .build();

        AcademyDirectorPOST post = new AcademyDirectorPOST(buildAcademyPOST, buildDirectorPOST);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/academy")
                        .contentType(APPLICATION_JSON)
                        .content(createJson(post))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("academy.name")
                                        .type(STRING)
                                        .description("학원 이름"),

                                fieldWithPath("academy.phone")
                                        .type(STRING)
                                        .optional()
                                        .description("학원 전화번호"),

                                fieldWithPath("academy.email")
                                        .type(STRING)
                                        .optional()
                                        .description("학원 이메일"),

                                fieldWithPath("academy.street")
                                        .type(STRING)
                                        .optional()
                                        .description("학원 주소"),

                                fieldWithPath("academy.addressDetail")
                                        .type(STRING)
                                        .optional()
                                        .description("학원 상세주소"),

                                fieldWithPath("academy.postalCode")
                                        .type(STRING)
                                        .optional()
                                        .description("학원 우편번호"),

                                fieldWithPath("academy.openToPublic")
                                        .type(BOOLEAN)
                                        .description("학원 공개 여부"),

                                fieldWithPath("director.loginId")
                                        .type(STRING)
                                        .description("학원장 로그인 아이디"),

                                fieldWithPath("director.loginPw")
                                        .type(STRING)
                                        .description("학원장 로그인 비밀번호"),

                                fieldWithPath("director.name")
                                        .type(STRING)
                                        .description("학원장 이름"),

                                fieldWithPath("director.email")
                                        .type(STRING)
                                        .description("학원장 이메일"),

                                fieldWithPath("director.phone")
                                        .type(STRING)
                                        .optional()
                                        .description("학원장 전화번호"),

                                fieldWithPath("director.street")
                                        .type(STRING)
                                        .optional()
                                        .description("학원장 주소"),

                                fieldWithPath("director.addressDetail")
                                        .type(STRING)
                                        .optional()
                                        .description("학원장 상세주소"),

                                fieldWithPath("director.postalCode")
                                        .type(STRING)
                                        .optional()
                                        .description("학원장 우편번호")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.directorId")
                                        .type(STRING)
                                        .description("등록된 학원장의 아이디")
                        )
                ));
    }

    // academy 의 uuid 를 검증하고 승인할 수 있다.
    @DisplayName("academy 의 uuid 를 검증하고 승인할 수 있다.")
    @Test
    void verifyAcademy() throws Exception {
        // mocking
        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
                .willReturn(true);

        // given
        Address anyAddress = uic.getAnyAddress();

        AcademyDirectorPOST.AcademyPOST buildAcademyPOST = AcademyDirectorPOST.AcademyPOST.builder()
                .name(uic.getUniqueAcademyName())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .street(anyAddress.getStreet())
                .addressDetail(anyAddress.getDetail())
                .postalCode(anyAddress.getPostalCode())
                .openToPublic(true)
                .build();

        AcademyDirectorPOST.DirectorPOST buildDirectorPOST = AcademyDirectorPOST.DirectorPOST.builder()
                .loginId(uic.getUniqueLoginId())
                .loginPw("P@ssw0rd")
                .name("홍길동")
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .street(anyAddress.getStreet())
                .addressDetail(anyAddress.getDetail())
                .postalCode(anyAddress.getPostalCode())
                .build();

        UUID directorId = refreshAnd(() -> academyCommandService.registerDirectorWithAcademy(buildAcademyPOST, buildDirectorPOST));
        Teacher getDirector = teacherQueryRepository.getById(directorId);
        UUID academyId = getDirector.getAcademy().getUuid();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/academy/{academyId}/verify", academyId.toString())
                        .contentType(APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("academyId").description("학원 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.verified")
                                        .type(BOOLEAN)
                                        .description("학원 승인 여부")
                        )
                ));
    }

    @DisplayName("academy 의 상세 정보를 조회할 수 있다.")
    @Test
    void getDetail() throws Exception {
        // given
        Academy academy = registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = registerTeacher(UserStatus.ACTIVE, academy);
        createRole(director, ROLE_DIRECTOR);
        Teacher teacher = registerTeacher(UserStatus.ACTIVE, academy);
        createRole(teacher, ROLE_TEACHER);

        em.flush();
        em.clear();
        String token = createToken(teacher);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/academy")
                        .contentType(APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .param("select", "teacher")
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("select")
                                        .description("""
                                                    같이 조회할 정보 옵션 +
                                                    - teacher : 학원에 소속된 선생 목록을 같이 조회
                                                    """)
                                        .optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.academy.id")
                                        .type(STRING)
                                        .description("학원 아이디"),

                                fieldWithPath("data.academy.name")
                                        .type(STRING)
                                        .description("학원 이름"),

                                fieldWithPath("data.academy.address.street")
                                        .type(STRING)
                                        .description("학원 주소 (도로명)"),

                                fieldWithPath("data.academy.address.detail")
                                        .type(STRING)
                                        .description("학원 주소 (상세주소)"),

                                fieldWithPath("data.academy.address.postalCode")
                                        .type(STRING)
                                        .description("학원 주소 (우편번호)"),

                                fieldWithPath("data.academy.phone")
                                        .type(STRING)
                                        .description("학원 전화번호"),

                                fieldWithPath("data.academy.email")
                                        .type(STRING)
                                        .description("학원 이메일"),

                                fieldWithPath("data.academy.openToPublic")
                                        .type(BOOLEAN)
                                        .description("학원 공개 여부"),

                                fieldWithPath("data.academy.status")
                                        .type(STRING)
                                        .description("학원 상태"),

                                fieldWithPath("data.academy.createdDateTime")
                                        .type(STRING)
                                        .description("학원 생성일시"),

                                fieldWithPath("data.academy.modifiedDateTime")
                                        .type(STRING)
                                        .description("학원 수정일시"),

                                fieldWithPath("data.teachers[].id")
                                        .type(STRING)
                                        .description("선생 아이디"),

                                fieldWithPath("data.teachers[].loginId")
                                        .type(STRING)
                                        .description("선생 로그인 아이디"),

                                fieldWithPath("data.teachers[].name")
                                        .type(STRING)
                                        .description("선생 이름"),

                                fieldWithPath("data.teachers[].email")
                                        .type(STRING)
                                        .description("선생 이메일"),

                                fieldWithPath("data.teachers[].emailVerified")
                                        .type(BOOLEAN)
                                        .description("선생 이메일 인증 여부"),

                                fieldWithPath("data.teachers[].phone")
                                        .type(STRING)
                                        .description("선생 전화번호"),

                                fieldWithPath("data.teachers[].address.street")
                                        .type(STRING)
                                        .description("선생 주소 (도로명)"),

                                fieldWithPath("data.teachers[].address.detail")
                                        .type(STRING)
                                        .description("선생 주소 (상세주소)"),

                                fieldWithPath("data.teachers[].address.postalCode")
                                        .type(STRING)
                                        .description("선생 주소 (우편번호)"),

                                fieldWithPath("data.teachers[].status")
                                        .type(STRING)
                                        .description("선생 상태"),

                                fieldWithPath("data.teachers[].roles[]")
                                        .type(ARRAY)
                                        .description("선생 권한"),

                                fieldWithPath("data.teachers[].lastPasswordChangeDateTime")
                                        .type(STRING)
                                        .description("선생 마지막 비밀번호 변경일시"),

                                fieldWithPath("data.teachers[].academyId")
                                        .type(STRING)
                                        .description("선생이 속한 학원 아이디"),

                                fieldWithPath("data.teachers[].createdDateTime")
                                        .type(STRING)
                                        .description("선생 생성일시"),

                                fieldWithPath("data.teachers[].modifiedDateTime")
                                        .type(STRING)
                                        .description("선생 수정일시"),

                                fieldWithPath("data.teachers[].createdBy")
                                        .type(STRING)
                                        .description("선생 생성자")
                                        .optional(),

                                fieldWithPath("data.teachers[].modifiedBy")
                                        .type(STRING)
                                        .description("선생 수정자")
                                        .optional()
                        )
                ));
    }

    // TODO: 2024-02-04 getPublicList, updateInfo 문서화
}
