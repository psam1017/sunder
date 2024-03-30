package psam.portfolio.sunder.english;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.docs.RestDocsConfig;
import psam.portfolio.sunder.english.infrastructure.mail.MailUtils;
import psam.portfolio.sunder.english.testbean.container.InfoContainer;
import psam.portfolio.sunder.english.testbean.data.DataCleaner;
import psam.portfolio.sunder.english.testbean.data.DataCreator;
import psam.portfolio.sunder.english.testbean.jpa.PersistenceContextManager;
import psam.portfolio.sunder.english.testconfig.TestConfig;

import java.util.function.Supplier;

@Slf4j
@ExtendWith(RestDocumentationExtension.class)
@Import({TestConfig.class, RestDocsConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class AbstractSunderApplicationTest {

	@Autowired
	protected InfoContainer infoContainer;

	@Autowired
	protected DataCreator dataCreator;

	@Autowired
	protected DataCleaner dataCleaner;

	@Autowired
	protected PersistenceContextManager persistenceContextManager;

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@MockBean
	protected MailUtils mailUtils;

	@BeforeEach
	void setUp() {
		dataCreator.createAllRoles();
	}

	protected String createJson(Object body) {
		try {
			return objectMapper.writeValueAsString(body);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	protected <T> T refreshAnd(Supplier<T> action) {
		return persistenceContextManager.refreshAnd(action);
	}

	protected void refreshAnd(Runnable action) {
		persistenceContextManager.refreshAnd(action);
	}

	protected void refresh() {
		persistenceContextManager.refresh();
	}
}
