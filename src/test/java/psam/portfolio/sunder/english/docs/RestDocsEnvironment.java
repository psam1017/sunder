package psam.portfolio.sunder.english.docs;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.global.security.filter.JwtAuthenticationFilter;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;
import psam.portfolio.sunder.english.domain.user.model.entity.User;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

// Import 와 ExtendWith 는 SunderApplicationTests 에서 수행. 그렇지 않으면 spring boot server 가 한 번 더 초기화됨.
//@Import(RestDocsConfig.class)
//@ExtendWith(RestDocumentationExtension.class)
public class RestDocsEnvironment extends AbstractSunderApplicationTest {

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @Autowired
    protected JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        // MockMvc 를 사용할 때는 Filter 를 직접 추가해야 한다.
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(provider))
                .addFilters(jwtAuthenticationFilter)
                .alwaysDo(MockMvcResultHandlers.print())
                .alwaysDo(restDocs)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    protected String createToken(User user) {
        return "Bearer " + jwtUtils.generateToken(user.getId().toString(), 1000 * 60);
    }
}
