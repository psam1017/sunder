package psam.portfolio.sunder.english.others.global;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 프론트 개발을 하는 동안에는 모든 Origin 의 접근을 허용하도록 설정한다.
public class SecurityConfigTest extends AbstractSunderApplicationTest {

    @DisplayName("허용하지 않는 Origin 의 접근을 막을 수 있다.")
    @Test
    void corsCrossOriginNotAllowed() throws Exception {
        // given
        String url = "/api/users/check-dupl?loginId=uid";

        // when
        ResultActions resultActions = mockMvc.perform(
                get(url)
                        .header("Access-Control-Request-Method", "GET")
                        .header("Origin", "https://www.google.com")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isForbidden());
    }

    // 빈으로 등록한 CorsConfigurationSource 에서 "https://ssunder.net" 를 허용함
    @DisplayName("프론트서버인 localhost:3000 을 Origin 으로 허용할 수 있다.")
    @Test
    void corsCrossOriginFrontEndAllowed() throws Exception {
        // given
        String url = "/api/users/check-dupl?loginId=uid";

        // when
        ResultActions resultActions = mockMvc.perform(
                get(url)
                        .header("Access-Control-Request-Method", "GET")
                        .header("Origin", "https://ssunder.net")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk());
    }
}
