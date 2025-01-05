package psam.portfolio.sunder.english.docs.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import psam.portfolio.sunder.english.docs.RestDocsEnvironment;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_DIRECTOR;

public class AcademyShareDocsTest extends RestDocsEnvironment {

    @DisplayName("교재를 공유할 학원을 추가할 수 있다.")
    @Test
    void share() throws Exception {
        // given
        Academy sharingAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, sharingAcademy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR);
        Academy sharedAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);

        String token = createBearerToken(director);

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/academies/{academyId}/shares/{sharedAcademyId}", sharingAcademy.getId(), sharedAcademy.getId())
                        .header(AUTHORIZATION, token)
                        .contentType(APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("academyId").description("공유할 학원 아이디"),
                                parameterWithName("sharedAcademyId").description("공유 받을 학원 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.sharedAcademyId").type(STRING).description("공유 받은 학원 아이디")
                        )
                ));
    }

    @DisplayName("교재를 공유 중인 학원 목록을 조회할 수 있다.")
    @Test
    void getShares() throws Exception {
        // given
        Academy sharingAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, sharingAcademy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR);
        Academy sharedAcademy1 = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Academy sharedAcademy2 = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        dataCreator.registerAcademyShare(sharingAcademy, sharedAcademy1);
        dataCreator.registerAcademyShare(sharingAcademy, sharedAcademy2);

        String token = createBearerToken(director);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/academies/{academyId}/shares", sharingAcademy.getId())
                        .header(AUTHORIZATION, token)
                        .contentType(APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("academyId").description("공유한 학원 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.shares[].academyId").type(STRING).description("공유 받은 학원 아이디"),
                                fieldWithPath("data.shares[].name").type(STRING).description("공유 받은 학원 이름"),
                                fieldWithPath("data.shares[].createdDateTime").type(STRING).description("공유 받은 일시")
                        )
                ));
    }

    @DisplayName("교재를 공유 중인 학원을 취소할 수 있다.")
    @Test
    void cancelShare() throws Exception {
        // given
        Academy sharingAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, sharingAcademy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR);
        Academy sharedAcademy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        dataCreator.registerAcademyShare(sharingAcademy, sharedAcademy);

        String token = createBearerToken(director);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/academies/{academyId}/shares/{sharedAcademyId}", sharingAcademy.getId(), sharedAcademy.getId())
                        .header(AUTHORIZATION, token)
                        .contentType(APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("academyId").description("공유한 학원 아이디"),
                                parameterWithName("sharedAcademyId").description("공유 받은 학원 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.sharedAcademyId").type(STRING).description("공유가 취소된 학원 아이디")
                        )
                ));
    }
}
