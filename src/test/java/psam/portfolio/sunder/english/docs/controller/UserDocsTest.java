package psam.portfolio.sunder.english.docs.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import psam.portfolio.sunder.english.docs.RestDocsEnvironment;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserDocsTest extends RestDocsEnvironment {

    // TODO: 2024-01-27 checkEmailDupl, checkPhoneDupl

    @DisplayName("user 의 loginId 중복체크를 할 수 있다.")
    @Test
    void checkLoginIdDupl() throws Exception {
        // given
        String loginId = "uid";

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/user/check-dupl")
                        .contentType(APPLICATION_JSON)
                        .param("loginId", loginId)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                                queryParameters(
                                        parameterWithName("loginId").description("중복체크할 아이디")
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("data.isOk")
                                                .type(BOOLEAN)
                                                .description("중복 검사 결과")
                                )
                        )
                );
    }
}
