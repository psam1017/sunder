package portfolio.sunder.global.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import portfolio.sunder.SunderApplicationTests;
import portfolio.sunder.web.student.dto.request.StudentSave;
import portfolio.sunder.web.user.enumeration.UserStatus;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SecurityConfigurationTest extends SunderApplicationTests {

    @DisplayName("허용하지 않는 Origin 의 접근을 막을 수 있다.")
    @Test
    void corsCrossOriginNotAllowed() throws Exception {
        // given
        StudentSave body = new StudentSave("uid", "upw", "name", UserStatus.ACTIVE, 1, 1, "school");

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/student")
                        .content(createJson(body))
                        .header("Access-Control-Request-Method", "POST")
                        .header("Origin", "http://www.google.com")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isForbidden());
    }

    // WebConfiguration 에서 "http://localhost:3000" 을 허용함
    @DisplayName("프론트서버인 localhost:3000 을 Origin 으로 허용할 수 있다.")
    @Test
    void corsCrossOriginFrontEndAllowed() throws Exception {
        // given
        StudentSave body = new StudentSave("uid", "upw", "name", UserStatus.ACTIVE, 1, 1, "school");

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/student")
                        .content(createJson(body))
                        .header("Access-Control-Request-Method", "POST")
                        .header("Origin", "http://localhost:3000")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk());
    }
}
