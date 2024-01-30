package psam.portfolio.sunder.english.docs;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import psam.portfolio.sunder.english.SunderApplicationTests;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

// Import 와 ExtendWith 는 SunderApplicationTests 에서 수행. 그렇지 않으면 spring boot server 가 한 번 더 초기화됨.
//@Import(RestDocsConfig.class)
//@ExtendWith(RestDocumentationExtension.class)
public class RestDocsEnvironment extends SunderApplicationTests {

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(provider))
                .alwaysDo(MockMvcResultHandlers.print())
                .alwaysDo(restDocs)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }
}
