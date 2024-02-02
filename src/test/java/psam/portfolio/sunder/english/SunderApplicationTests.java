package psam.portfolio.sunder.english;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
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
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;
import psam.portfolio.sunder.english.testbean.ConcurrentUniqueInfoContainer;
import psam.portfolio.sunder.english.testbean.TestConfig;
import psam.portfolio.sunder.english.testbean.UniqueInfoContainer;
import psam.portfolio.sunder.english.web.teacher.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;
import psam.portfolio.sunder.english.web.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.web.teacher.repository.AcademyCommandRepository;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;

import java.util.function.Supplier;

@Slf4j
@ExtendWith(RestDocumentationExtension.class)
@Import({TestConfig.class, RestDocsConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class SunderApplicationTests {

	@Test
	void contextLoads() {

	}

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper om;

	@Autowired
	protected EntityManager em;

	@Autowired
	protected UniqueInfoContainer uic;

	@MockBean
	protected MailUtils mailUtils;

	@Autowired
	private TeacherCommandRepository teacherCommandRepository;

	@Autowired
	private AcademyCommandRepository academyCommandRepository;

	@Autowired
	private PasswordUtils passwordUtils;

	protected String createJson(Object body) {
		try {
			return om.writeValueAsString(body);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T runWithRefresh(Supplier<T> action) {
		log.info("========== Flush and Clear before Action ==========");
		em.flush();
		em.clear();
		T result = action.get();
		em.flush();
		em.clear();
		log.info("========== Flush and Clear after Action ==========");
		return result;
	}

	protected Academy registerAcademy(AcademyStatus status) {
		Academy academy = Academy.builder()
				.name(uic.getUniqueAcademyName())
				.address(uic.getAnyAddress())
				.phone(uic.getUniquePhoneNumber())
				.email(uic.getUniqueEmail())
				.openToPublic(true)
				.status(status)
				.build();
		return academyCommandRepository.save(academy);
	}

	protected Teacher registerTeacher(UserStatus status, Academy academy) {
		String uniqueId = uic.getUniqueId();
		Teacher teacher = Teacher.builder()
				.loginId(uniqueId)
				.loginPw(passwordUtils.encode("qwe123!@#"))
				.name("사용자" + uniqueId.substring(0, 3))
				.email(uic.getUniqueEmail())
				.emailVerified(true)
				.phone(uic.getUniquePhoneNumber())
				.status(status)
				.academy(academy)
				.build();
		return teacherCommandRepository.save(teacher);
	}
}
